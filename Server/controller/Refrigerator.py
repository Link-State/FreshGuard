import sys
import os
import hashlib
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from config import Config
from datetime import datetime
from static import app
from werkzeug.utils import secure_filename
from werkzeug.datastructures import FileStorage
from controller.helper import *
from model.User import User
from model.Detail import Detail
from model.Refrigerator import Refrigerator
from model.Ingredient import Ingredient
from model.Discernment import Discernment
from model.Photo import Photo
from sqlalchemy.orm import Query
from sqlalchemy import and_, or_

def load_ingredient(user_uid:int) :
    refrigerators:list[Query] = Refrigerator.query.filter(Refrigerator.user_id==user_uid).all()
    list_ingredient = list()
    for refri in refrigerators :
        item = dict()
        detail = Detail.query.filter(Detail.id==refri.id).first()
        ingre = Ingredient.query.filter(Ingredient.id==detail.ingredient_id).first()
        expire_date = ""
        image_url = ""
        if detail.photo_id != None :
            photo = Photo.query.filter(Photo.id==detail.photo_id).first()
            image_dir = str(photo.image)
            image_name = os.path.basename(image_dir)
            image_url = f"{Config.ImageURL}/{image_name}"
        if detail.expire_date != None :
            date_formatting(dt=detail.expire_date)
        item["id"] = refri.id
        item["ingredient_num"] = ingre.id
        item["level"] = detail.fresh_level
        item["expire"] = expire_date
        item["created"] = date_formatting(dt=detail.created)
        item["image"] = image_url
        list_ingredient.append(item)
    
    return {"ingredients" : list_ingredient}

def add_ingredient(user_uid:int, level:int, ingre:int, expire:str|None, discernment_id:int|None, img:FileStorage|None) :
    imageDirectory = None
    photo = None
    photo_id = None

    if discernment_id != None :
        dis = Discernment.query.filter(Discernment.id==discernment_id).first()
        if dis != None :
            photo_id = int(dis.reference_photo_id)

    if img != None :
        _, ext = os.path.splitext(img.filename)
        fileName = str(user_uid) + "-A-" + "-" + str(ingre) + "-" + str(level) + "-" + datetime.now().strftime("%Y%m%d%H%M%S%f")
        sha256 = hashlib.sha256()
        sha256.update(fileName.encode())
        hash_result = sha256.hexdigest()
        hashedFileName = hash_result + ext
        imageDirectory = os.path.join(app.instance_path, "images", secure_filename(hashedFileName))
        img.save(imageDirectory)
        photo = Photo.add(imageDirectory)
        photo_id = photo.id
    
    detail = Detail.add(level, ingre, expire, discernment_id, photo_id)
    Refrigerator.add(user_uid, detail.id)

    return {"success" : True, "ingredient_id" : detail.id}

def modify_ingredient(ingredient_id:int, ingredient_num:int|None, level:int|None, expire:str|None, image:FileStorage|None) :
    detail = Detail.query.filter(Detail.id==ingredient_id).first()
    if detail == None :
        return {"success" : False}
    
    hasChange = False
    
    if ingredient_num != None :
        ingredient = Ingredient.query.filter(Ingredient.id==ingredient_id).first()
        if ingredient == None :
            return {"success" : False}
        hasChange = True
    
    if level != None :
        hasChange = True
    
    if expire != None :
        hasChange = True
    
    photo_id = None
    if image != None :
        _, ext = os.path.splitext(image.filename)
        fileName = str(ingredient_id) + "-B-" + "-" + str(detail.ingredient_id) + "-" + str(detail.fresh_level) + "-" + datetime.now().strftime("%Y%m%d%H%M%S%f")
        sha256 = hashlib.sha256()
        sha256.update(fileName.encode())
        hash_result = sha256.hexdigest()
        hashedFileName = hash_result + ext
        imageDirectory = os.path.join(app.instance_path, "images", secure_filename(hashedFileName))
        image.save(imageDirectory)
        photo = Photo.add(imageDirectory)
        photo_id = photo.id
        hasChange = True
    
    if hasChange :
        Detail.modify(id=detail.id, lv=level, ingre=ingredient_num, expd=expire, pid=photo_id)
    
    return {"success" : True}

def delete_ingredient(ingredient_id:int) :
    detail = Detail.query.filter(Detail.id==ingredient_id).first()
    if detail == None :
        return {"success" : False}

    refri = Refrigerator.query.filter(Refrigerator.detail_id==detail.id).first()
    if refri == None :
        return {"success" : False}
    
    Refrigerator.delete(refri.id)
    Detail.delete(detail.id)
    return {"success" : True}
