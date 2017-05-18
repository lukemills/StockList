import json

from bson import ObjectId
from flask import request

from api import Data
from database.data.child import lookup, delete, edit


class Child(Data):
    def patch(self, child_id):
        operations = ["add", "remove"]

        commands = request.values.to_dict()
        if len(commands) < 1:
            return ["submit changes in the form of", {"op": "val", "args": "val"}], 400

        if len(commands) == 1:
            for key in commands:
                commands = json.loads(key)
                break

        for instruction in commands:

            try:
                assert isinstance(instruction, dict)
            except AssertionError:
                return {"must pass commands as a dictionary, eg": [{"op": "1"}, {"op": "2"}]}, 400
            if "op" not in instruction:
                # not a valid instruction
                return {"no keyword op in received:": instruction}, 400
            if instruction['op'] not in operations:
                return {"valid operations are": operations}, 400

            if instruction['op'] == 'add':
                if 'id' not in instruction:
                    return {"need field \'id\'": instruction}, 400
                else:
                    items = lookup(child_id)[0]['items']
                    print("items is", items)
                    items.append(ObjectId(instruction['id']))

                    return edit(child_id, items=items)

            if instruction['op'] == 'remove':
                if 'id' not in instruction:
                    return {"need field \'id\'": instruction}, 400
                else:
                    items = lookup(child_id)[0]['items']
                    print("items is", items)
                    try:
                        items.remove(ObjectId(instruction['id']))
                    except ValueError:
                        return {"not in list": instruction['id']}, 404

                    return edit(child_id, items=items)

    def delete(self, child_id):

        return delete(child_id)

    def get(self, child_id):
        (rv, x) = lookup(child_id)

        return rv, x
