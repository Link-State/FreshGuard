import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, Integer, Date, ForeignKey


class Detail(database.Model) :
    __tablename__ = "Detail"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    fresh_level = Column(Integer, nullable=False)
    ingredient_id = Column(Integer, ForeignKey('Ingredient.id'), nullable=False)
    created = Column(Date, nullable=False)
    expire_date = Column(Date, nullable=True)
    discernment_id = Column(Integer, ForeignKey('Discernment.id'), nullable=True)
    photo_id = Column(Integer, ForeignKey('Photo.id'), nullable=True)

    def __init__(self, lv:int, ingre:int, expd=None, did=None, photo=None, created="") :
        self.fresh_level = lv
        self.ingredient_id = ingre
        if len(created) == 0 :
            self.created = datetime.now().strftime("%Y-%m-%d")
        else :
            self.created = created
        self.expire_date = expd
        self.discernment_id = did
        self.photo_id = photo
        return
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def modify(id:int|Column[int], lv:int|Column[int]|None, ingre:int|Column[int]|None, expd:str|None, pid:int|Column[int]|None) :
        detail = Detail.query.filter(Detail.id==id).first()

        if ingre != None :
            detail.ingredient_id = ingre
        
        if lv != None :
            detail.fresh_level = lv
        
        if expd != None :
            detail.expire_date = expd
        
        if pid != None :
            detail.photo_id = pid
        database.session.commit()
        return
    
    @staticmethod
    def delete(id:int|Column[int]) :
        detail = Detail.query.filter(Detail.id==id).first()
        if detail == None :
            return False
        database.session.delete(detail)
        database.session.commit()
        return True
    
    @staticmethod
    def add(lv:int, ingre:int|Column[int], expd=None, did=None, photo=None, created="") :
        detail = Detail(lv, ingre, expd, did, photo, created)
        detail.commit()
        return detail
