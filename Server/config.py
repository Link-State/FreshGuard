
class Config() :
    SECRETKEY:str = "IWANTREST"
    ContentMax:int = 16 * 1024 * 1024
    Domain:str = "http://freshguard.kro.kr"
    IP:str = "34.10.101.220"
    Host:str = "127.0.0.1"
    Port:int = 8000
    MySQLPort:int = 3306
    DataBaseUser:str = "capstone"
    DataBasePWD:str = "MASKED"
    DataBaseName:str = "capstone"
    ImageURL:str = f"http://{IP}:{Port}/image"
    DataBaseURI:str = f"mysql+pymysql://{DataBaseUser}:{DataBasePWD}@{IP}:{MySQLPort}/{DataBaseName}"
    APIKEY:str = "MASKED"
    RecipeAPIURL:str = f"http://openapi.foodsafetykorea.go.kr/api/{APIKEY}/COOKRCP01/json"
