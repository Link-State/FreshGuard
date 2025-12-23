import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, Integer, Date, String

class Photo(database.Model) :
    __tablename__ = "Photo"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    image = Column(String(1024), nullable=False)
    created = Column(Date, nullable=False)

    def __init__(self, img:str, created="") :
        self.image = img
        if len(created) == 0 :
            self.created = datetime.now().strftime("%Y-%m-%d")
        else :
            self.created = created
        return
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def add(img:str, created="") :
        photo = Photo(img, created)
        photo.commit()
        return photo