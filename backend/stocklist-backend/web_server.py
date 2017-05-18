import json

from bson import ObjectId
from flask import Flask
from flask import make_response
from flask_restful import Api, Resource

from api.collection.child import Child as CChild
from api.collection.item import Item as CItem
from api.collection.parent import Parent as CParent
from api.collection.user import User as CUser
from api.data.child import Child as DChild
from api.data.item import Item as DItem
from api.data.parent import Parent as DParent
from api.data.user import User as DUser
from api.filter import Filter
<<<<<<< HEAD
# from config import host, port
from flask import Flask
from flask_cors import CORS, cross_origin

host = 'localhost'
port = 5000

app = Flask(__name__)

#cors = CORS(app, resorces={r'/d/*': {"origins": '*'}})
CORS(app)
=======
from config import host, port
from flask import Flask
from flask_cors import CORS, cross_origin

app = Flask(__name__)

#cors = CORS(app, resorces={r'/d/*': {"origins": '*'}})
cors = CORS(app, resources=r'/*')
>>>>>>> master

class JSONEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, ObjectId):
            return str(o)
        return json.JSONEncoder.default(self, o)


def output_json(obj, code, headers=None):
    """
    This is needed because we need to use a custom JSON converter
    that knows how to translate MongoDB types to JSON.
    """
    resp = make_response(JSONEncoder().encode(obj), code)
    resp.headers.extend(headers or {})

    return resp


DEFAULT_REPRESENTATIONS = {'application/json': output_json}

app = Flask(__name__)
api = Api(app)
api.representations = DEFAULT_REPRESENTATIONS

# Endpoints

user_collection = "/api/user"
parent_collection = "/api/parent"
child_collection = "/api/child"
item_collection = "/api/item"

user_specific = user_collection + "/<string:user_id>"
parent_specific = parent_collection + "/<string:parent_id>"
child_specific = child_collection + "/<string:child_id>"
item_specific = item_collection + "/<string:item_id>"

# TEMP STUFF

with open('static.json') as data_file:
    data = json.load(data_file)


class HelloWorld(Resource):
    @staticmethod
    def get():
        return data


api.add_resource(HelloWorld, '/')

# END TEMP STUFF

api.add_resource(CUser, user_collection, endpoint="users")
api.add_resource(CParent, parent_collection, endpoint="parents")
api.add_resource(CChild, child_collection, endpoint="children")
api.add_resource(CItem, item_collection, endpoint="items")

api.add_resource(DUser, user_specific, endpoint="user")
api.add_resource(DParent, parent_specific, endpoint="parent")
api.add_resource(DChild, child_specific, endpoint="child")
api.add_resource(DItem, item_specific, endpoint="item")

api.add_resource(Filter, '/api/filter/<string:user_id>/<string:list_id>')

if __name__ == '__main__':
    app.run(debug=True, host=host, port=port)
