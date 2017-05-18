import bson
from bson import ObjectId
from flask import request, json

from api import Data
from database.data.user import lookup, delete, edit


class User(Data):
    def patch(self, user_id):
        operations = ["add", "remove", "edit"]

        fields = ["last_name",
                  "first_name"]

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
                    items = lookup(user_id)[0]['lists']
                    print("lists are", items)
                    try:
                        items.append(ObjectId(instruction['id']))
                    except bson.errors.InvalidId:
                        return [instruction['id'], "is not a valid id"], 400

                    return edit(user_id, lists=items)

            if instruction['op'] == 'remove':
                if 'id' not in instruction:
                    return {"need field \'id\'": instruction}, 400
                else:
                    items = lookup(user_id)[0]['lists']
                    print("lists are", items)
                    try:
                        items.remove(ObjectId(instruction['id']))
                    except bson.errors.InvalidId:
                        return [instruction['id'], "is not a valid id"], 400
                    except ValueError:
                        return {"not in list": instruction['id']}, 404

                    return edit(user_id, lists=items)

            if instruction['op'] == 'edit':

                to_pass = {key: instruction[key] for key in instruction if key != "op" and key in fields}
                if to_pass == {}:
                    return {"No valid field given": instruction}, 400
                return edit(user_id, **to_pass)

    def delete(self, user_id):
        return delete(user_id)

    def get(self, user_id):
        return lookup(user_id)
