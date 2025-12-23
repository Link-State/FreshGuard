import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from flask import session
from sqlalchemy import and_, or_
from model.User import User
from model.Refrigerator import Refrigerator

# 로그인
def login(user_id:str, user_pwd:str) :
    user = User.query.filter(and_(User.user_id==user_id, User.password==user_pwd)).first()
    if user == None :
        return {"success" : False, "user_uid" : -1}
    
    dict_user:dict = user.__dict__

    session[str(dict_user["id"])] = True
    return {"success" : True, "user_uid" : dict_user["id"]}

# 회원가입
def signup(user_id:str, user_pwd:str, email:str) :
    hasUser = User.query.filter(User.user_id==user_id).first()
    if hasUser != None :
        return {"success" : False}
    
    User.add(user_id, user_pwd, email)
    
    return {"success" : True}

# 아이디 중복체크
def id_check(user_id:str) :
    user = User.query.filter(User.user_id==user_id).first()
    if user == None :
        return {"isExist" : False}
    return {"isExist" : True}
