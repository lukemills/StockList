from flask import request

from api import Collection
from database.collection.item import list_items, create


class Item(Collection):
    def get(self):
        return list_items(), 200

    def post(self):
        expected = ["name",
                    "description",
                    "quantity",
                    "unitOfMeasure",
                    "brand",
                    "image",
                    "upc"]
        rv = {}
        commands = request.values.to_dict()
        item = {}

        for field in expected:
            try:
                item[field] = commands[field]
            except KeyError:
                rv[field] = "Not provided"

        if len(item) == len(expected):
            iid = create(**item)
            return {"id": iid}, 400
        else:
            return rv, 200
