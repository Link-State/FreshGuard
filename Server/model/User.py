import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, Integer, String, Date

class User(database.Model) :
    __tablename__ = "User"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    user_id = Column(String(128), nullable=False)
    password = Column(String(128), nullable=False)
    email = Column(String(128), nullable=False)
    signup = Column(Date, nullable=False)

    def __init__(self, uid:str, pwd:str, em:str, sign="") :
        self.user_id = uid
        self.password = pwd
        self.email = em
        
        if len(sign) == 0 :
            self.signup = datetime.now().strftime("%Y-%m-%d")
        else :
            self.signup = sign
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def delete(id:int|Column[int]) :
        user = User.query.filter(User.id==id).first()
        if user == None :
            return False
        database.session.delete(user)
        database.session.commit()
        return True
    
    @staticmethod
    def add(uid:str, pwd:str, em:str, sign="") :
        user = User(uid, pwd, em, sign)
        user.commit()
        return user