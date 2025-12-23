import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from config import Config
import pymysql

CONNECTION = None

def initialize() :
    global CONNECTION
    CONNECTION = pymysql.connect(
        host=Config.IP,
        user=Config.DataBaseUser,
        password=Config.DataBasePWD,
        database=Config.DataBaseName,
        port=Config.MySQLPort,
        use_unicode=True,
        charset="utf8",
        cursorclass=pymysql.cursors.DictCursor
    )
    print("Connected MySQL.")

    command = CONNECTION.cursor()

    command.execute("DROP TABLE IF EXISTS Refrigerator")
    command.execute("DROP TABLE IF EXISTS Recipe")
    command.execute("DROP TABLE IF EXISTS Detail")
    command.execute("DROP TABLE IF EXISTS DiscernmentSession")
    command.execute("DROP TABLE IF EXISTS Discernment")
    command.execute("DROP TABLE IF EXISTS Photo")
    command.execute("DROP TABLE IF EXISTS Ingredient")
    command.execute("DROP TABLE IF EXISTS User")
    print("Clear all tables.")

    command.execute("""CREATE TABLE Detail
      (
        id             INT  NOT NULL AUTO_INCREMENT COMMENT '식재료 세부 고유번호',
        fresh_level    INT  NOT NULL COMMENT '현재 신선도',
        expire_date    DATE NULL     COMMENT '소비기한',
        discernment_id INT  NULL     COMMENT '판별 기록 고유번호',
        ingredient_id  INT  NOT NULL COMMENT '식재료 고유번호',
        photo_id       INT  NULL     COMMENT '사진 고유번호',
        created        DATE NOT NULL COMMENT '추가일',
        PRIMARY KEY (id)
      ) COMMENT '식재료 세부 테이블';""")
    
    command.execute("""CREATE TABLE Discernment
      (
        id                  INT   NOT NULL AUTO_INCREMENT COMMENT '판별 기록 고유번호',
        user_id             INT   NOT NULL COMMENT '유저 고유번호',
        fresh_level         INT   NOT NULL COMMENT '신선도',
        confident           FLOAT NOT NULL COMMENT '신뢰도',
        probability_fresh   FLOAT NOT NULL COMMENT '신선확률',
        probability_rotten  FLOAT NOT NULL COMMENT '부패확률',
        ingredient_id       INT   NOT NULL COMMENT '식재료 고유번호',
        origin_photo_id     INT   NOT NULL COMMENT '식재료 원본사진 고유번호',
        reference_photo_id  INT   NULL     COMMENT '식재료 참고사진 고유번호',
        created             DATE  NOT NULL COMMENT '판별일',
        PRIMARY KEY (id)
      ) COMMENT '판별 기록 테이블';""")
    
    command.execute("""CREATE TABLE DiscernmentSession
      (
        id       INT  NOT NULL AUTO_INCREMENT COMMENT '판별세션 고유번호',
        state    INT  NOT NULL COMMENT '판별상태',
        photo_id INT  NOT NULL COMMENT '사진 고유번호',
        start    DATE NOT NULL COMMENT '판별시작일',
        PRIMARY KEY (id)
      ) COMMENT '판별세션 테이블';""")
    
    command.execute("""CREATE TABLE Ingredient
      (
        id   INT     NOT NULL AUTO_INCREMENT COMMENT '식재료 고유번호',
        name VARCHAR(128) NOT NULL COMMENT '식재료 이름',
        PRIMARY KEY (id)
      ) COMMENT '식재료 테이블';""")
    
    command.execute("""ALTER TABLE Ingredient
      ADD CONSTRAINT UQ_name UNIQUE (name);""")
    
    command.execute("""CREATE TABLE Photo
      (
        id      INT           NOT NULL AUTO_INCREMENT COMMENT '사진 고유번호',
        image   VARCHAR(1024) NOT NULL COMMENT '사진 경로',
        created DATE          NOT NULL COMMENT '생성일',
        PRIMARY KEY (id)
      ) COMMENT '사진 테이블';""")
    
    command.execute("""CREATE TABLE Recipe
      (
        id       INT     NOT NULL AUTO_INCREMENT COMMENT '조리법 고유번호',
        user_id  INT     NOT NULL COMMENT '유저 고유번호',
        sequence INT     NOT NULL COMMENT '일련번호',
        name     VARCHAR(128) NOT NULL COMMENT '메뉴명',
        type     VARCHAR(128) NOT NULL COMMENT '요리종류',
        created  DATE    NOT NULL COMMENT '추가일',
        PRIMARY KEY (id)
      ) COMMENT '조리법 테이블';""")
    
    command.execute("""CREATE TABLE Refrigerator
      (
        id        INT NOT NULL AUTO_INCREMENT COMMENT '냉장고 고유번호',
        user_id   INT NOT NULL COMMENT '유저 고유번호',
        detail_id INT NOT NULL COMMENT '식재료 세부 고유번호',
        PRIMARY KEY (id)
      ) COMMENT '냉장고 테이블';""")
    
    command.execute("""CREATE TABLE User
      (
        id       INT     NOT NULL AUTO_INCREMENT COMMENT '유저 고유번호',
        user_id  VARCHAR(128) NOT NULL COMMENT '유저 아이디',
        password VARCHAR(128) NOT NULL COMMENT '비밀번호',
        email    VARCHAR(128) NOT NULL COMMENT '이메일',
        signup   DATE    NOT NULL COMMENT '가입일',
        PRIMARY KEY (id)
      ) COMMENT '유저 테이블';""")
    
    command.execute("""ALTER TABLE Refrigerator
      ADD CONSTRAINT FK_User_TO_Refrigerator
        FOREIGN KEY (user_id)
        REFERENCES User (id);""")
    
    command.execute("""ALTER TABLE Discernment
      ADD CONSTRAINT FK_User_TO_Discernment
        FOREIGN KEY (user_id)
        REFERENCES User (id);""")
    
    command.execute("""ALTER TABLE Discernment
      ADD CONSTRAINT FK_Ingredient_TO_Discernment
        FOREIGN KEY (ingredient_id)
        REFERENCES Ingredient (id);""")
    
    command.execute("""ALTER TABLE Discernment
      ADD CONSTRAINT FK_OriginPhoto_TO_Discernment
        FOREIGN KEY (origin_photo_id)
        REFERENCES Photo (id);""")
    
    command.execute("""ALTER TABLE Discernment
      ADD CONSTRAINT FK_ReferencePhoto_TO_Discernment
        FOREIGN KEY (reference_photo_id)
        REFERENCES Photo (id);""")
    
    command.execute("""ALTER TABLE Detail
      ADD CONSTRAINT FK_Ingredient_TO_Detail
        FOREIGN KEY (ingredient_id)
        REFERENCES Ingredient (id);""")
    
    command.execute("""ALTER TABLE Refrigerator
      ADD CONSTRAINT FK_Detail_TO_Refrigerator
        FOREIGN KEY (detail_id)
        REFERENCES Detail (id);""")
    
    command.execute("""ALTER TABLE Detail
      ADD CONSTRAINT FK_Discernment_TO_Detail
        FOREIGN KEY (discernment_id)
        REFERENCES Discernment (id);""")
    
    command.execute("""ALTER TABLE Recipe
      ADD CONSTRAINT FK_User_TO_Recipe
        FOREIGN KEY (user_id)
        REFERENCES User (id);""")
    
    command.execute("""ALTER TABLE Detail
      ADD CONSTRAINT FK_Photo_TO_Detail
        FOREIGN KEY (photo_id)
        REFERENCES Photo (id);""")
    
    command.execute("""ALTER TABLE DiscernmentSession
      ADD CONSTRAINT FK_Photo_TO_DiscernmentSession
        FOREIGN KEY (photo_id)
        REFERENCES Photo (id);""")
    print("Create new tables.")

    command.execute("""INSERT INTO Ingredient (name) VALUE ('사과');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('바나나');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('오렌지');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('피망');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('당근');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('오이');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('망고');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('감자');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('딸기');""")
    command.execute("""INSERT INTO Ingredient (name) VALUE ('토마토');""")
    print("Insert initial data.")

    CONNECTION.commit()
    CONNECTION.close()

    print("Initialized DB.")


if __name__ == "__main__" :
    initialize()
