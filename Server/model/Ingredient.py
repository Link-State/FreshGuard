import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from static import database
from sqlalchemy import Column, Integer, String

class Ingredient(database.Model) :
    __tablename__ = "Ingredient"

    id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    name = Column(String(128), nullable=False, unique=True)

    def __init__(self, name:str) :
        self.name = name
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def add(name:str) :
        ingre = Ingredient(name)
        ingre.commit()
        return ingre