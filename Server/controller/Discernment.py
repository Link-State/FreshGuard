import sys
import os
import hashlib
import asyncio
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
import FreshGuard
from controller.helper import *
from config import Config
from datetime import datetime
from static import app
from werkzeug.utils import secure_filename
from werkzeug.datastructures import FileStorage
from model.User import User
from model.Discernment import Discernment
from model.DiscernmentSession import DiscernmentSession
from model.Photo import Photo
from sqlalchemy.orm import Query

def discernment_ingredient(user_uid:int, image:FileStorage) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"success" : False, "session_id" : -1}
    
    _, ext = os.path.splitext(image.filename)
    fileName = str(user_uid) + "-C-" + datetime.now().strftime("%Y%m%d%H%M%S%f")
    sha256 = hashlib.sha256()
    sha256.update(fileName.encode())
    hash_result = sha256.hexdigest()
    hashedFileName = hash_result + ext
    imageDirectory = os.path.join(app.instance_path, "images", secure_filename(hashedFileName))
    image.save(imageDirectory)
    photo = Photo.add(imageDirectory)
    photo_id = int(photo.id)

    session = DiscernmentSession.add(0, photo_id)
    session_id = int(session.id)

    # AI에게 판별 요청 (비동기)
    asyncio.run(FreshGuard.discernment(int(user.id), session_id, imageDirectory))
    
    return {"success" : True, "session_id" : session_id}

def load_discernment(user_uid:int, session_id:int) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"success" : False, "result" : []}
    
    session = DiscernmentSession.query.filter(DiscernmentSession.id==session_id).first()
    if session == None :
        return {"success" : False, "result" : []}
    
    state = int(session.state)
    if state > 1 :
        return {"success" : True, "result" : []}
    
    origin_photo_id = int(session.photo_id)
    
    result = list()
    discernment:list[Query] = Discernment.query.filter(Discernment.origin_photo_id==origin_photo_id).all()
    if len(discernment) <= 0 :
        return {"success" : False, "result" : []}

    for dis in discernment :
        rr = dict()
        rr["discernment_id"] = int(dis.id)
        if dis.reference_photo_id == None :
            rr["image"] = ""
        else :
            photo = Photo.query.filter(Photo.id==dis.reference_photo_id).first()
            image_name = os.path.basename(str(photo.image))
            image_url = f"{Config.ImageURL}/{image_name}"
            rr["image"] = image_url
        rr["ingre_num"] = int(dis.ingredient_id)
        rr["date"] = date_formatting(dt=dis.created)
        rr["level"] = int(dis.fresh_level)
        result.append(rr)

    return {"success" : True, "result" : result}

def load_history(user_uid:int) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"history" : []}
    
    result = list()
    discernment = Discernment.query.filter(Discernment.user_id==user_uid).all()
    for dis in discernment :
        rr = dict()
        image_url = ""
        if dis.reference_photo_id != None :
            photo = Photo.query.filter(Photo.id==dis.reference_photo_id).first()
            image_name = os.path.basename(str(photo.image))
            image_url = f"{Config.ImageURL}/{image_name}"
        rr["dcm_id"] = int(dis.id)
        rr["ingre_num"] = int(dis.ingredient_id)
        rr["dcm_date"] = date_formatting(dt=dis.created)
        rr["level"] = int(dis.fresh_level)
        rr["image"] = image_url
        result.append(rr)

    return {"history" : result}
