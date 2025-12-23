# FreshGuard
### [2025 2학기 캡스톤디자인]

### 개발 기간
> 2025.09.01 ~ 2025.12.05

### 개발 환경
> App : Android Kotlin<br>
> Server : Ubuntu 24.04 LTS + MySQL + Python 3.12.6 (Flask)<br>
> AI : Kaggle + PyTorch(YOLOv2 + Efficient Net)<br>

### 역할
팀장, UI 디자인, 서버 구축, 앱-서버 API 연동

### 설명
+ 동기
    + 현재 대한민국은 1인 가구가 증가함에 따라 판매되는 식재료들 또한 1인 가구에 맞춰 소량으로 판매되는 추세이다. 그러나 모든 식재료가 소량으로 판매되지 않으며 오히려 대량 구매 시 상대적으로 비용이 저렴하다. 1인 가구는 다인 가구에 비해 식재료 관리에 소홀해질 수 있는데, 한국농촌경제연구원 조사에 따르면 1인 가구는 “보관방법 몰라 상한 식재료”의 비중이 다인 가구에 비해 높았다고 한다.(한두봉, 『2024 식품소비행태조사 기초분석보고서』, 한국농촌경제연구원(2024), 472.) 이처럼 현재 집에 있는 식재료의 신선도를 가늠하기 어려워 버리게 되는 불편감과 추가로 대형마트에서 식료품을 살 때 식재료의 신선도와 관련된 정보를 잘 알지 못해 겪은 불편감을 해소하기 위해 식자재 판별 및 관리 시스템을 구축하고자 한다.
+ 기획
  + 사용자가 스마트폰을 이용하여 식재료를 촬영, 이후 판별 및 관리를 위한 안드로이드 애플리케이션을 개발
  + 안드로이드 개발언어는 Kotlin, 서버는 Linux 환경의 Ubuntu를 채택, RESTful API를 위한 서버 프레임워크는 Flask, 데이터베이스는 MySQL 사용
  + 앱-서버 간 API 호출을 원활하게 하기 위한 문서 정리
  + AI는 PyTorch를 사용하여 모델 구축, Kaggle을 통해 식재료 데이터 습득
  + YOLOv2를 통해 객체 탐지, Efficient Net을 통해 탐지한 객체에 대해서 식재료 종류 + 신선도 분류
  + 사과, 바나나, 오렌지, 피망, 망고, 토마토, 감자, 당근, 오이, 딸기 총 10가지 식재료를 판별하며 식재료 품질에 대해 좋음, 보통, 나쁨 총 3단계로 분류

### 실행결과

진입화면<br>
<img width="270" height="585" alt="Picture3" src="https://github.com/user-attachments/assets/7d305f7e-1abf-4dd5-95be-0714cd7145e8" />

회원가입<br>
<img width="270" height="585" alt="Picture4" src="https://github.com/user-attachments/assets/ddd77ece-b38a-498f-ab53-5d37ccf60dfd" />

로그인<br>
<img width="270" height="585" alt="Picture5" src="https://github.com/user-attachments/assets/1ea76181-93f2-4032-b811-b5005088ef0a" />

메인화면<br>
<img width="270" height="585" alt="Picture6" src="https://github.com/user-attachments/assets/e02bbca2-6e6b-47b1-89ca-1ec2e9ecdddd" />

보관 중인 식재료<br>
<img width="270" height="585" alt="Picture7" src="https://github.com/user-attachments/assets/648ada48-d39d-47af-a3d7-4dedb42bb463" />

최근 검사 기록 목록<br>
<img width="270" height="585" alt="Picture8" src="https://github.com/user-attachments/assets/11dac48b-3c8c-4ccc-8a48-c4726472f113" />

식재료 상세보기<br>
<img width="270" height="585" alt="Picture9" src="https://github.com/user-attachments/assets/ec5bc3ba-0f89-42ea-8f5f-a8c903c09864" />

식재료 추가<br>
<img width="270" height="585" alt="Picture10" src="https://github.com/user-attachments/assets/15a5f16b-0f6d-446b-9f21-fc73fe514b7d" />

식재료 판별기록 상세보기<br>
<img width="270" height="585" alt="Picture11" src="https://github.com/user-attachments/assets/d94daa53-269d-4e64-a985-fc961ece78f8" />

저장 중인 레시피 목록<br>
<img width="270" height="585" alt="Picture12" src="https://github.com/user-attachments/assets/1dff6e8e-d36e-4a86-8d53-9ff9d337bba8" />

추천 레시피 목록<br>
<img width="270" height="585" alt="Picture13" src="https://github.com/user-attachments/assets/665b513d-6089-4ad0-90ed-c27ed7cb8dcf" />

레시피 상세보기<br>
<img width="270" height="585" alt="Picture14" src="https://github.com/user-attachments/assets/b74119e3-9dec-4a51-a0c1-04f7dd326b4f" />

식재료 판별을 위한 이미지 불러오기<br>
<img width="270" height="585" alt="Picture15" src="https://github.com/user-attachments/assets/8b72125b-7470-4eef-ba56-74db0cbd3432" />

식재료 판별 중<br>
<img width="270" height="585" alt="Picture16" src="https://github.com/user-attachments/assets/8c0be499-0914-4500-9a3b-0108952bebfa" />

식재료 판별 결과<br>
<img width="270" height="585" alt="Picture17" src="https://github.com/user-attachments/assets/7476b0f7-0127-4def-92f8-b2dd4fc3d7b3" />

<br>

