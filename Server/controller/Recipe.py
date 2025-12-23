import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
import requests
from config import Config
from controller.helper import *
from model.User import User
from model.Refrigerator import Refrigerator
from model.Detail import Detail
from model.Ingredient import Ingredient
from model.Recipe import Recipe
from sqlalchemy.orm import Query
from sqlalchemy import and_, or_

def load_recipe_list(user_uid:int) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"recipes" : []}
    
    recipes:list[Query] = Recipe.query.filter(Recipe.user_id==user.id).all()
    list_recipes = list()
    for rip in recipes :
        dict_recipe = dict()
        dict_recipe["id"] = rip.id
        dict_recipe["sequence"] = rip.sequence
        dict_recipe["name"] = rip.name
        dict_recipe["type"] = rip.type
        dict_recipe["created"] = date_formatting(dt=rip.created)
        list_recipes.append(dict_recipe)

    return {"recipes" : list_recipes}

def load_recipe_detail(user_uid:int, name:str, sequence:int) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"recipe" : {}}
    
    keyword_name = str(name).replace(" ", "")

    start = 1
    end = 1000
    context = None
    while context == None :
        url = f"{Config.RecipeAPIURL}/{start}/{end}/RCP_NM={keyword_name}"

        response = None
        try :
            response = requests.get(url, timeout=15)
        except requests.exceptions.ReadTimeout :
            return {"recipe" : {}}

        if response.status_code != 200 :
            return {"recipe" : {}}
        api_result:dict = response.json()

        api_response = api_result["COOKRCP01"]["RESULT"]["CODE"]
        if api_response != "INFO-000" :
            return {"recipe" : {}}

        recipe_list:list[dict] = api_result["COOKRCP01"]["row"]
        for rip in recipe_list :
            if int(rip["RCP_SEQ"]) == sequence :
                context = rip
                break
        start = end + 1
        end += 1000
    if context == None :
        return {"recipe" : {}}
    
    result = dict()
    result["sequence"] = sequence
    result["name"] = context["RCP_NM"]
    result["ingredients"] = context["RCP_PARTS_DTLS"]

    guides = list()
    for i in range(1, 21) :
        step = dict()
        manual = context["MANUAL{:0>2}".format(str(i))]
        manual_img = context["MANUAL_IMG{:0>2}".format(str(i))]

        if manual == "" :
            break

        step["text"] = manual
        step["image"] = manual_img
        guides.append(step)
    result["guides"] = guides

    return {"recipe" : result}

def add_recipe(user_uid:int, seq:int, name:str) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"success" : False, "recipe_id" : -1}
    
    keyword_name = str(name).replace(" ", "")

    start = 1
    end = 1000
    context = None
    while context == None :
        url = f"{Config.RecipeAPIURL}/{start}/{end}/RCP_NM={keyword_name}"

        response = None
        try :
            response = requests.get(url, timeout=15)
        except requests.exceptions.ReadTimeout :
            return {"success" : False, "recipe_id" : -1}
        
        if response.status_code != 200 :
            return {"success" : False, "recipe_id" : -1}
        api_result:dict = response.json()

        api_response = api_result["COOKRCP01"]["RESULT"]["CODE"]
        if api_response != "INFO-000" :
            return {"success" : False, "recipe_id" : -1}

        recipe_list:list[dict] = api_result["COOKRCP01"]["row"]
        for rip in recipe_list :
            if int(rip["RCP_SEQ"]) == seq :
                context = rip
                break
        
        start = end + 1
        end += 1000
    if context == None :
        return {"success" : False, "recipe_id" : -1}
    
    rep = Recipe.query.filter(and_(Recipe.sequence==seq, Recipe.name==name)).first()
    if rep != None :
        return {"success" : False, "recipe_id" : -1}
    
    recipe = Recipe.add(uid=user.id, seq=seq, name=name, type=context["RCP_PAT2"])
    return {"success" : True, "recipe_id" : int(recipe.id)}

def delete_recipe(recipe_id:int) :
    recipe = Recipe.query.filter(Recipe.id==recipe_id).first()
    if recipe == None :
        return {"success" : False}
    
    Recipe.delete(recipe.id)
    return {"success" : True}

def recommend_recipe(user_uid:int) :
    user = User.query.filter(User.id==user_uid).first()
    if user == None :
        return {"recipes" : []}
    
    saved_recipe_query:list[Query] = Recipe.query.filter(Recipe.user_id==user.id).all()
    saved_recipe = dict()
    for rep in saved_recipe_query :
        saved_recipe[str(rep.sequence)] = str(rep.id)
    
    refrigerator:list[Query] = Refrigerator.query.filter(Refrigerator.user_id==user.id).all()

    ingre = dict()
    for ref in refrigerator :
        detail = Detail.query.filter(Detail.id==ref.detail_id).first()
        ingredient = Ingredient.query.filter(Ingredient.id==detail.ingredient_id).first()
        ingre[str(ingredient.name)] = True

    if len(ingre) <= 0 :
        return {"recipes" : []}
    
    recommend_recipe_list = list()
    for name in ingre.keys() :
        start = 1
        end = 1000
        while True :
            url = f"{Config.RecipeAPIURL}/{start}/{end}/RCP_NM={name}"

            response = None
            try :
                response = requests.get(url, timeout=15)
            except requests.exceptions.ReadTimeout :
                return {"recipes" : []}
            
            if response.status_code != 200 :
                return {"recipes" : []}

            api_result:dict = response.json()
            api_response = api_result["COOKRCP01"]["RESULT"]["CODE"]
            if api_response != "INFO-000" : break

            recipe_list:list[dict] = api_result["COOKRCP01"]["row"]
            for rip in recipe_list :
                saved = False
                rep_id = -1
                if str(rip["RCP_SEQ"]) in saved_recipe :
                    saved = True
                    rep_id = int(saved_recipe[str(rip["RCP_SEQ"])])
                rr = dict()
                rr["id"] = rep_id
                rr["sequence"] = int(rip["RCP_SEQ"])
                rr["name"] = str(rip["RCP_NM"])
                rr["saved"] = saved
                recommend_recipe_list.append(rr)
            
            start = end + 1
            end += 1000
    
    return {"recipes" : recommend_recipe_list}
