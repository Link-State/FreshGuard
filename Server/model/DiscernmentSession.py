import sys
import os
sys.path.append(os.path.abspath(os.path.dirname(__file__)))
from datetime import datetime
from static import database
from sqlalchemy import Column, INTEGER, DATE, ForeignKey

class DiscernmentSession(database.Model) :
    __tablename__ = "DiscernmentSession"

    id = Column(INTEGER, primary_key=True, nullable=False, autoincrement=True)
    state = Column(INTEGER, nullable=False)
    photo_id = Column(INTEGER, ForeignKey('Photo.id'), nullable=False)
    start = Column(DATE, nullable=False)

    def __init__(self, state:int, photo:int|Column[int], start="") :
        self.state = state
        self.photo_id = photo
        if len(start) == 0 :
            self.start = datetime.now().strftime("%Y-%m-%d")
        else :
            self.start = start
    
    def commit(self) :
        database.session.add(self)
        database.session.commit()
        return
    
    @staticmethod
    def modify(id:int|Column[int], state:int) :
        session = DiscernmentSession.query.filter(DiscernmentSession.id==id).first()

        if state < 0 or state > 2 :
            return
        
        session.state = state
        database.session.commit()
        return
    
    @staticmethod
    def add(state:int, photo:int|Column[int], start="") :
        discern = DiscernmentSession(state, photo, start)
        discern.commit()
        return discern
