import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, Integer, Date, String, ForeignKey

class Recipe(database.Model) :
    __tablename__ = "Recipe"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    user_id = Column(Integer, ForeignKey('User.id'), nullable=False)
    sequence = Column(Integer, nullable=False)
    name = Column(String(128), nullable=False)
    type = Column(String(128), nullable=False)
    created = Column(Date, nullable=False)

    def __init__(self, uid:int|Column[int], seq:int, name:str, type:str, created="") :
        self.user_id = uid
        self.sequence = seq
        self.name = name
        self.type = type
        if len(created) == 0 :
            self.created = datetime.now().strftime("%Y-%m-%d")
        else :
            self.created = created
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def delete(id:int|Column[int]) :
        recipe = Recipe.query.filter(Recipe.id==id).first()
        if recipe == None :
            return False
        database.session.delete(recipe)
        database.session.commit()
        return True
    
    @staticmethod
    def add(uid:int|Column[int], seq:int, name:str, type:str, created="") :
        recipe = Recipe(uid, seq, name, type, created)
        database.session.add(recipe)
        database.session.commit()
        return recipe