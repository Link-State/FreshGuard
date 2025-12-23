import sys
import os
from datetime import datetime

def date_formatting(**kwarg) -> str :
    """
    지정된 날짜를 문자열 포맷팅
    
    :param dt: 포맷팅할 datetime 객체
    :type dt: datetime
    :return: "2025.01.01 (수)"와 같이 포맷팅된 날짜 문자열
    :rtype: str
    """

    dt = datetime.now()
    if "dt" in kwarg :
        dt = kwarg["dt"]
    
    result = dt.strftime("%Y.%m.%d (%a)")
    
    return result

def string_date_formatting(date_str:str) -> str :
    """
    날짜문자열을 포맷팅
    
    :param date_str: "20250101"과 같은 날짜 문자열
    :type date_str: str
    :return: "2025.01.01 (수)"와 같은 포맷팅된 날짜 문자열
    :rtype: str
    """

    if len(date_str) < 8 :
        raise Exception("포맷형식이 알맞지 않습니다.")
    
    target_str:str = date_str[:8]
    remain_str:str = date_str[8:]

    if not target_str.isdecimal() :
        raise Exception("날짜는 숫자만 허용됩니다.")
    
    year:int = int(target_str[:4])
    month:int = int(target_str[4:6])
    day:int = int(target_str[6:])
    hour:int = 0
    minute:int = 0
    second:int = 0

    return date_formatting(dt=datetime(year, month, day, hour, minute, second))