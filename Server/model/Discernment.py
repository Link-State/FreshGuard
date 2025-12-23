import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, INTEGER, FLOAT, DATE, ForeignKey

class Discernment(database.Model) :
    __tablename__ = "Discernment"

    id = Column(INTEGER, primary_key=True, nullable=False, autoincrement=True)
    user_id = Column(INTEGER, ForeignKey('User.id'), nullable=False)
    fresh_level = Column(INTEGER, nullable=False)
    confident = Column(FLOAT, nullable=False)
    probability_fresh = Column(FLOAT, nullable=False)
    probability_rotten = Column(FLOAT, nullable=False)
    ingredient_id = Column(INTEGER, ForeignKey('Ingredient.id'), nullable=False)
    origin_photo_id = Column(INTEGER, ForeignKey('Photo.id'), nullable=False)
    reference_photo_id = Column(INTEGER, ForeignKey('Photo.id'), nullable=True)
    created = Column(DATE, nullable=False)

    def __init__(self, uid:int|Column[int], lv:int, conf:float, p_fresh:float, p_rotten:float, ingre:int|Column[int], origin_photo:int|Column[int], ref_photo:int|Column[int], created="") :
        self.user_id = uid
        self.fresh_level = lv
        self.confident = conf
        self.probability_fresh = p_fresh
        self.probability_rotten = p_rotten
        self.ingredient_id = ingre
        self.origin_photo_id = origin_photo
        self.reference_photo_id = ref_photo
        if len(created) == 0 :
            self.created = datetime.now().strftime("%Y-%m-%d")
        else :
            self.created = created
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def add(uid:int|Column[int], lv:int, conf:float, p_fresh:float, p_rotten:float, ingre:int|Column[int], origin_photo:int|Column[int], ref_photo:int|Column[int], created="") :
        discern = Discernment(uid, lv, conf, p_fresh, p_rotten, ingre, origin_photo, ref_photo, created)
        discern.commit()
        return discern
