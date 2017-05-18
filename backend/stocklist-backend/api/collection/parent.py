from flask import request

from api import Collection
from database.collection.parent import create, list_parents


class Parent(Collection):
    def post(self):
        commands = request.values.to_dict()

        try:
            list_name = commands['list_name']
            parent_id = create(list_name)
            return {"parent_id": parent_id}, 200
        except KeyError:
            return {"list_name": "Not provided"}, 400

    def get(self):
        return list_parents(), 200
