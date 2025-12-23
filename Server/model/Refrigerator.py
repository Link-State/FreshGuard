import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from static import database
from sqlalchemy import Column, Integer, ForeignKey

class Refrigerator(database.Model) :
    __tablename__ = "Refrigerator"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    user_id = Column(Integer, ForeignKey('User.id'), nullable=False)
    detail_id = Column(Integer, ForeignKey('Detail.id'), nullable=False)

    def __init__(self, uid:int|Column[int], did:int|Column[int]) :
        self.user_id = uid
        self.detail_id = did
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def delete(id:int|Column[int]) :
        refri = Refrigerator.query.filter(Refrigerator.id==id).first()
        if refri == None :
            return False
        database.session.delete(refri)
        database.session.commit()
        return True
    
    @staticmethod
    def add(uid:int|Column[int], did:int|Column[int]) :
        refri = Refrigerator(uid, did)
        refri.commit()
        return refri
