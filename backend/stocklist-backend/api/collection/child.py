from flask import request

from api import Collection
from database.collection.child import create, list_children


class Child(Collection):
    def post(self):
        commands = request.values.to_dict()

        try:
            list_name = commands['list_name']
            child_id = create(list_name)
            return {"child_id": child_id}, 200
        except KeyError:
            return {"list_name": "Not provided"}, 400

    def get(self):
        return list_children(), 200
