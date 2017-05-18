from flask import json, request

from api import Data
from database.data.parent import lookup, delete, edit


class Parent(Data):
    def get(self, parent_id):

        # rv['items'] = (list(map(lambda i: child.lookup(i)[0], rv['items'])))

        return lookup(parent_id)

    def patch(self, parent_id):
        operations = ["rename"]

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

            if instruction['op'] == 'rename':
                if 'name' not in instruction:
                    return {"need field \'name\'": instruction}, 400
                else:

                    return edit(parent_id, **{'list_name': instruction['name']})

    def delete(self, parent_id):

        delete(parent_id)
