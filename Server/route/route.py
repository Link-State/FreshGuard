import sys
import os
import static
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from flask import jsonify, request, send_file, render_template
from flask_restful import Resource
from flask_cors import CORS
from controller.Account import *
from controller.Discernment import *
from controller.Refrigerator import *
from controller.Recipe import *


class Login(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_id" not in data or "user_pwd" not in data :
            return jsonify({"success" : False, "user_uid" : -1})
        
        id = str(data["user_id"]).strip()
        pwd = str(data["user_pwd"]).strip()
        result = login(user_id=id, user_pwd=pwd)

        return jsonify(result)


class Signup(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_id" not in data or "user_pwd" not in data or "email" not in data :
            return jsonify({"success" : False})
        
        id = str(data["user_id"]).strip()
        pwd = str(data["user_pwd"]).strip()
        email = str(data["email"]).strip()
        result = signup(user_id=id, user_pwd=pwd, email=email)
        
        return jsonify(result)


class IdCheck(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_id" not in data :
            return jsonify({"isExist" : False})
        
        id = str(data["user_id"]).strip()
        result = id_check(user_id=id)

        return jsonify(result)


class DiscernmentIngredient(Resource) :
    def post(self) :
        data = request.form
        if "user_uid" not in data or "image" not in request.files :
            return jsonify({"success" : False, "session_id" : -1})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"success" : False, "session_id" : -1})

        file = request.files["image"]
        fileName = file.filename
        if fileName == "" or fileName == None :
            return jsonify({"success" : False, "session_id" : -1})
        
        filetype = file.content_type
        if filetype == None :
            return jsonify({"success" : False, "session_id" : -1})
        
        if filetype.find("image") != 0 :
            return jsonify({"success" : False, "session_id" : -1})
        
        uid = int(data["user_uid"])
        result = discernment_ingredient(user_uid=uid, image=file)

        return jsonify(result)


class LoadIngredient(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data :
            return jsonify({"ingredients" : []})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"ingredients" : []})
        
        uid = int(data["user_uid"])
        result = load_ingredient(user_uid=uid)

        return jsonify(result)


class AddIngredient(Resource) :
    def post(self) :
        data = request.form
        if "user_uid" not in data or "level" not in data or "ingredient_num" not in data :
            return jsonify({"success" : False, "ingredient_id" : -1})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"success" : False, "ingredient_id" : -1})
        
        if not str(data["level"]).isdecimal() :
            return jsonify({"success" : False, "ingredient_id" : -1})
        
        if not str(data["ingredient_num"]).isdecimal() :
            return jsonify({"success" : False, "ingredient_id" : -1})
        
        uid = int(data["user_uid"])
        lv = int(data["level"])
        ingre = int(data["ingredient_num"])

        expire=None
        if "expire" in data :
            expire = str(data["expire"])
        
        discernment_id=None
        if "discernment_id" in data and str(data["discernment_id"]).isdecimal() :
            discernment_id = int(data["discernment_id"])
        
        image=None
        if "image" in request.files :
            fileName = request.files["image"].filename
            fileType = str(request.files["image"].content_type)
            if fileName != None and fileName != "" and fileType.find("image") == 0 :
                image = request.files["image"]
        
        result = add_ingredient(user_uid=uid, level=lv, ingre=ingre, expire=expire, discernment_id=discernment_id, img=image)

        return jsonify(result)


class ModifyIngredient(Resource) :
    def post(self) :
        data = request.form
        if "ingredient_id" not in data :
            return jsonify({"success" : False})
        
        if not str(data["ingredient_id"]).isdecimal() :
            return jsonify({"success" : False})
        
        ingredient_id = int(str(data["ingredient_id"]))
        
        ingredient_num = None
        if "ingredient_num" in data and str(data["ingredient_num"]).isdecimal() :
            ingredient_num = int(str(data["ingredient_num"]))
        
        level = None
        if "level" in data and str(data["level"]).isdecimal() :
            level = int(str(data["level"]))

        expire = None
        if "expire" in data :
            expire = str(data["expire"])
        
        image = None
        if "image" in request.files :
            fileName = request.files["image"].filename
            fileType = str(request.files["image"].content_type)
            if fileName != None and fileName != "" and fileType.find("image") == 0 :
                image = request.files["image"]
        
        result = modify_ingredient(ingredient_id=ingredient_id, ingredient_num=ingredient_num, level=level, expire=expire, image=image)

        return jsonify(result)


class DeleteIngredient(Resource) :
    def post(self) :
        data = request.get_json()
        if "ingredient_id" not in data  :
            return jsonify({"success" : False})
        
        if not str(data["ingredient_id"]).isdecimal() :
            return jsonify({"success" : False})
        
        ingredient_id = int(data["ingredient_id"])
        result = delete_ingredient(ingredient_id=ingredient_id)

        return jsonify(result)


class LoadDiscernment(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data or "session_id" not in data :
            return jsonify({"success" : False, "result" : []})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"success" : False, "result" : []})
        
        if not str(data["session_id"]).isdecimal() :
            return jsonify({"success" : False, "result" : []})
        
        uid = int(data["user_uid"])
        session_id = int(data["session_id"])
        result = load_discernment(user_uid=uid, session_id=session_id)

        return jsonify(result)


class LoadHistory(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data :
            return jsonify({"history" : []})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"history" : []})
        
        user_uid = int(data["user_uid"])
        result = load_history(user_uid=user_uid)

        return jsonify(result)


class LoadRecipeList(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data and not str(data["user_uid"]).isdecimal() :
            return jsonify({"recipes" : []})
        user_uid = int(data["user_uid"])
        result = load_recipe_list(user_uid=user_uid)
        return jsonify(result)


class LoadRecipeDetail(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data or "name" not in data or "sequence" not in data :
            return jsonify({"recipe" : []})
        
        if not str(data["user_uid"]).isdecimal() or not str(data["sequence"]).isdecimal() :
            return jsonify({"recipe" : []})
        
        user_uid = int(data["user_uid"])
        name = str(data["name"])
        sequence = int(data["sequence"])

        result = load_recipe_detail(user_uid=user_uid, name=name, sequence=sequence)
        return jsonify(result)


class AddRecipe(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data or "sequence" not in data or "name" not in data :
            return jsonify({"success" : False, "recipe_id" : -1})
        
        if not str(data["user_uid"]).isdecimal() or not str(data["sequence"]).isdecimal() :
            return jsonify({"success" : False, "recipe_id" : -1})
        
        user_uid = int(data["user_uid"])
        sequence = int(data["sequence"])
        name = str(data["name"])
        
        result = add_recipe(user_uid=user_uid, seq=sequence, name=name)
        return jsonify(result)


class DeleteRecipe(Resource) :
    def post(self) :
        data = request.get_json()
        if "recipe_id" not in data :
            return jsonify({"success" : False})
        
        if not str(data["recipe_id"]).isdecimal() :
            return jsonify({"success" : False})
        
        recipe_id = int(data["recipe_id"])

        result = delete_recipe(recipe_id=recipe_id)
        return jsonify(result)


class RecommendRecipe(Resource) :
    def post(self) :
        data = request.get_json()
        if "user_uid" not in data :
            return jsonify({"recipes" : []})
        
        if not str(data["user_uid"]).isdecimal() :
            return jsonify({"recipes" : []})
        
        user_uid = int(data["user_uid"])
        
        result = recommend_recipe(user_uid=user_uid)
        return jsonify(result)


class ImageRoute(Resource) :
    def get(self, imgName:str) :
        imgDir = os.path.join(static.app.instance_path, "images", imgName)
        if not os.path.exists(imgDir) :
            return send_file(os.path.join(static.app.instance_path, "images", "404_NotFound.bmp"))
        return send_file(os.path.join(static.app.instance_path, "images", imgName))


class Test(Resource) :
    def get(self) :
        result = None

        return jsonify({"test" : result})
