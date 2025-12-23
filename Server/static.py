from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app:Flask = Flask(__name__)
database:SQLAlchemy = SQLAlchemy()