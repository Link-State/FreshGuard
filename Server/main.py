import os
from config import Config
from static import database, app
from flask_restful import Api
from route import route

# flask
# flask-cors
# flask-restful
# flask-sqlalchemy
# pymysql
# requests
# ultralytics
# torchvision
# cryptography

app.config['SECRET_KEY'] = Config.SECRETKEY
app.config['SQLALCHEMY_DATABASE_URI'] = Config.DataBaseURI
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
app.config['UPLOAD_FOLDER'] = os.path.join(app.instance_path, "images")
app.config['MAX_CONTENT_PATH'] = Config.ContentMax

os.makedirs(os.path.join(app.instance_path, "images"), exist_ok=True)

database.init_app(app)

api = Api(app)
api.add_resource(route.Login, '/login')
api.add_resource(route.Signup, '/signup')
api.add_resource(route.IdCheck, '/id-check')
api.add_resource(route.DiscernmentIngredient, '/discernment-ingredient')
api.add_resource(route.LoadIngredient, '/load-ingredient')
api.add_resource(route.AddIngredient, '/add-ingredient')
api.add_resource(route.ModifyIngredient, '/modify-ingredient')
api.add_resource(route.DeleteIngredient, '/delete-ingredient')
api.add_resource(route.LoadDiscernment, '/load-discernment')
api.add_resource(route.LoadHistory, '/load-history')
api.add_resource(route.LoadRecipeList, '/load-recipe-list')
api.add_resource(route.LoadRecipeDetail, '/load-recipe-detail')
api.add_resource(route.AddRecipe, '/add-recipe')
api.add_resource(route.DeleteRecipe, '/delete-recipe')
api.add_resource(route.RecommendRecipe, '/recommend-recipe')
api.add_resource(route.ImageRoute, '/image/<string:imgName>')
api.add_resource(route.Test, '/t-e-s-t')

if __name__ == '__main__':
    app.run(Config.Host, port=Config.Port, debug=True)

