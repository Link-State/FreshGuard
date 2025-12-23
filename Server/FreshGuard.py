# ============================================
#  YOLOv8n + EfficientNet 멀티태스크 (이미지 파일용)
#  - YOLOv8n: 객체 감지 -> bounding box만 사용
#  - EfficientNet: crop -> 과일 10종 + fresh/normal/rotten 3단계
#  - IoU 기반 박스 중복 제거 + 썩은 개수/신뢰도 콘솔 요약
# ============================================
import os
import hashlib
import asyncio
from static import app
from model.Photo import Photo
from model.Discernment import Discernment
from model.DiscernmentSession import DiscernmentSession
from datetime import datetime
import cv2
import torch
import torch.nn as nn
import torch.nn.functional as F
from pathlib import Path
from ultralytics import YOLO
from torchvision import transforms
from PIL import Image
from werkzeug.utils import secure_filename

FRUIT = {"apple" : 1, "banana" : 2, "orange" : 3, "bell_pepper" : 4, "carrot" : 5, "cucumber" : 6, "mango" : 7, "potato" : 8, "strawberry" : 9, "tomato" : 10}
FRESH_LEVEL = {"fresh" : 3, "normal" : 2, "rotten" : 1}

IMAGE_SAVE_DIR = Path(os.path.join(app.instance_path, "images"))
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# -----------------------------
# 1) EfficientNet 멀티태스크 weight 경로
# -----------------------------
EFFNET_BEST_PATH = Path(os.path.abspath(os.path.join(app.instance_path, "..", "efficientnet_b0_freshguard_multitask.pt"))) # 경로는 수정해주세요.

# 2) EfficientNet 체크포인트 로드
ckpt = torch.load(EFFNET_BEST_PATH, map_location=DEVICE)
fruit_names  = ckpt["fruit_names"]      # ['apple', 'banana', ...]
fresh_labels = ckpt["fresh_labels"]     # ['fresh', 'rotten']
fresh_label2idx = {lab: i for i, lab in enumerate(fresh_labels)}
idx_fresh  = fresh_label2idx["fresh"]
idx_rotten = fresh_label2idx["rotten"]

# -----------------------------
# 3) EfficientNet 멀티태스크 모델
# -----------------------------
class MultiTaskEffB0(nn.Module):
    def __init__(self, n_fruit: int, n_fresh: int):
        super().__init__()
        from torchvision.models import efficientnet_b0, EfficientNet_B0_Weights
        weights = EfficientNet_B0_Weights.IMAGENET1K_V1
        backbone = efficientnet_b0(weights=weights)
        in_ch = backbone.classifier[1].in_features
        backbone.classifier[1] = nn.Identity()
        self.backbone = backbone
        self.dropout = nn.Dropout(0.2)
        self.head_fruit = nn.Linear(in_ch, n_fruit)
        self.head_fresh = nn.Linear(in_ch, n_fresh)  # 2 클래스 (fresh / rotten)

    def forward(self, x):
        feat = self.backbone(x)
        feat = self.dropout(feat)
        return self.head_fruit(feat), self.head_fresh(feat)

effnet_model = MultiTaskEffB0(n_fruit=len(fruit_names), n_fresh=len(fresh_labels)).to(DEVICE)
effnet_model.load_state_dict(ckpt["model_state"])
effnet_model.eval()

# -----------------------------
# 4) EfficientNet inference용 transform (val과 동일)
# -----------------------------
mean = [0.485, 0.456, 0.406]
std  = [0.229, 0.224, 0.225]
effnet_transform = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(224),
    transforms.ToTensor(),
    transforms.Normalize(mean, std),
])

# -----------------------------
# 5) YOLOv8n 사전학습 weight 로드 (COCO pretrain, box만 사용)
# -----------------------------
yolo_model = YOLO("yolov8n.pt")  # 처음 호출 시 자동 다운로드됨

# -----------------------------
# 6) fresh 확률 -> fresh/normal/rotten 3단계로 매핑
# -----------------------------
THRESH_FRESH_HIGH = 0.7
THRESH_FRESH_LOW  = 0.3

def prob_to_3class(p_fresh: float) -> str:
    if p_fresh >= THRESH_FRESH_HIGH:
        return "fresh"
    elif p_fresh <= THRESH_FRESH_LOW:
        return "rotten"
    else:
        return "normal"

# -----------------------------
# 7) IoU + 박스 필터링 (중복/노이즈 제거)
# -----------------------------
def box_iou(b1, b2):
    # b: (x1,y1,x2,y2,score)
    x1 = max(b1[0], b2[0])
    y1 = max(b1[1], b2[1])
    x2 = min(b1[2], b2[2])
    y2 = min(b1[3], b2[3])

    inter_w = max(0, x2 - x1)
    inter_h = max(0, y2 - y1)
    inter = inter_w * inter_h
    if inter <= 0:
        return 0.0

    area1 = (b1[2] - b1[0]) * (b1[3] - b1[1])
    area2 = (b2[2] - b2[0]) * (b2[3] - b2[1])
    return inter / (area1 + area2 - inter + 1e-6)

def filter_boxes(boxes, iou_thres=0.7, min_area=800):
    """
    boxes: list of (x1,y1,x2,y2,score)
    - 너무 작은 박스 제거
    - IoU가 높은 중복 박스 제거 (score 높은 것 우선)
    """
    boxes = sorted(boxes, key=lambda x: x[4], reverse=True)
    picked = []
    for b in boxes:
        x1, y1, x2, y2, score = b
        area = (x2 - x1) * (y2 - y1)
        if area < min_area:
            continue
        keep = True
        for pb in picked:
            if box_iou(b, pb) > iou_thres:
                keep = False
                break
        if keep:
            picked.append(b)
    return picked

# -----------------------------
# 8) crop → EfficientNet 멀티태스크 예측
# -----------------------------
def classify_crop_bgr(img_bgr):
    """
    YOLO가 뽑아준 crop(BGR)을 EfficientNet 멀티태스크에 넣어서:
      - 과일 이름 (fruit_names 중 하나)
      - fresh/normal/rotten (3단계)
      - p_fresh, p_rotten
      - 상태 기준 conf (신뢰도)
      을 돌려준다.
    """
    img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
    pil_img = Image.fromarray(img_rgb)
    x = effnet_transform(pil_img).unsqueeze(0).to(DEVICE)

    with torch.no_grad():
        pf, pb = effnet_model(x)
        fruit_idx = pf.argmax(1).item()
        probs = F.softmax(pb, dim=1)[0].cpu().numpy()
        p_fresh  = float(probs[idx_fresh])
        p_rotten = float(probs[idx_rotten])

        fresh_3 = prob_to_3class(p_fresh)

        if fresh_3 == "fresh":
            conf = p_fresh
        elif fresh_3 == "rotten":
            conf = p_rotten
        else:
            conf = 1.0 - abs(p_fresh - 0.5) * 2  # 0~1

    return fruit_names[fruit_idx], fresh_3, p_fresh, p_rotten, conf



# -----------------------------
# 9) 메인: 이미지 한 장 처리
# -----------------------------
# ┌──────────────── 2025/11/17 14:35 JYH 수정 ───────────────────────────────
def run_on_image(session_id:int, image_path:str|Path, conf_thres=0.25) -> list :
    """
    단일 이미지 파일에서:
      1) YOLOv8n으로 bounding box 감지
      2) IoU 기반 중복/작은 박스 제거
      3) 각 box crop -> EfficientNet 멀티태스크로 과일 + fresh/normal/rotten 예측
      4) 결과를 bounding box와 라벨로 시각화
      5) 콘솔에 "n번 과일 / 상태 / 신뢰도" 요약 출력
    """
    frame = cv2.imread(image_path)
    if frame is None:
        print("이미지를 읽을 수 없다:", image_path)
        return list()

    results = yolo_model(frame, conf=conf_thres, verbose=False)

    raw_boxes = []
    for r in results:
        for box in r.boxes:
            x1, y1, x2, y2 = box.xyxy[0].cpu().numpy().astype(int)
            h, w, _ = frame.shape
            x1, y1 = max(0, x1), max(0, y1)
            x2, y2 = min(w, x2), min(h, y2)
            if x2 <= x1 or y2 <= y1:
                continue
            score = float(box.conf.item())
            raw_boxes.append((x1, y1, x2, y2, score))

    clean_boxes = filter_boxes(raw_boxes, iou_thres=0.7, min_area=800)

    summary:list[dict] = []

    if not clean_boxes:
        print("[요약] 감지된 유효한 객체가 없습니다.")
        return list()

    for det_idx, b in enumerate(clean_boxes, start=1):
        x1, y1, x2, y2, score = b
        crop = frame[y1:y2, x1:x2]
        if crop.size == 0:
            continue

        fruit_pred, fresh_3, p_fresh, p_rotten, conf = classify_crop_bgr(crop)
        label = f"{det_idx}: {fruit_pred} / {fresh_3} ({conf*100:.1f}%)"

        if fresh_3 == "fresh":
            color = (0, 255, 0)
        elif fresh_3 == "normal":
            color = (0, 255, 255)
        else:
            color = (0, 0, 255)

        area = ((y1, y2), (x1, x2))

        summary.append({
            "idx": det_idx,
            "fruit": fruit_pred,
            "state": fresh_3,
            "conf": conf,
            "p_fresh": p_fresh,
            "p_rotten": p_rotten,
            "area" : area
        })
    
    # 이미지 저장
    _, ext = os.path.splitext(image_path)
    fileName = str(session_id) + "-D-" + datetime.now().strftime("%Y%m%d%H%M%S%f")
    sha256 = hashlib.sha256()
    sha256.update(fileName.encode())
    hash_result = sha256.hexdigest()
    hashedFileName = hash_result + ext

    for i in range(len(summary)):
        idx:int = summary[i]['idx']
        area:tuple[tuple] = summary[i]['area']

        y1:int = area[0][0]
        y2:int = area[0][1]
        x1:int = area[1][0]
        x2:int = area[1][1]
        partial = frame[y1:y2, x1:x2].copy()

        hashedFileName = f"{hash_result}({idx}){ext}"
        img_name = secure_filename(hashedFileName)
        img_path = Path(os.path.join(IMAGE_SAVE_DIR, img_name))
        cv2.imwrite(img_path, partial)
        summary[i]["image"] = img_path
    
    return summary

# -----------------------------
# 사용 예시 (이미지 경로만 바꿔서 쓰면 됨)
# -----------------------------
# run_on_image('/content/test_fruit.jpg', save_path='/content/test_fruit_out.jpg')

async def discernment(user_id:int, session_id:int, image_path:str|Path) :
    result:list[dict] = run_on_image(session_id, image_path)

    if len(result) <= 0 :
        DiscernmentSession.modify(session_id, 2)
        return
    
    session = DiscernmentSession.query.filter(DiscernmentSession.id==session_id).first()
    origin_photo_id = int(session.photo_id)

    for obj in result :
        state = FRESH_LEVEL[obj["state"]]
        fruit = FRUIT[obj["fruit"]]
        conf = obj["conf"]
        p_fresh = obj["p_fresh"]
        p_rotten = obj["p_rotten"]
        image = obj["image"]

        ref_photo = Photo.add(image)
        ref_photo_id = int(ref_photo.id)
        Discernment.add(user_id, state, conf, p_fresh, p_rotten, fruit, origin_photo_id, ref_photo_id)

    DiscernmentSession.modify(session_id, 1)
    return