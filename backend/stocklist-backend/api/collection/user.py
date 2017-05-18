from flask import request

from api import Collection
from database.collection.user import create, list_users


class User(Collection):
    def post(self):
        expected = ["username", "first_name", "last_name"]
        rv = {}
        commands = request.values.to_dict()
        person = {}

        for field in expected:
            try:
                person[field] = commands[field]
            except KeyError:
                rv[field] = "Not provided"

        if len(person) == len(expected):
            uid = create(**person)
            return {"id": uid}, 400
        else:
            return rv, 200

    def get(self):
        return list_users(), 200
