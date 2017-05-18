import json
import pprint

from flask import request

from api import Data
from database.data.item import lookup, delete, edit, inc, dec


class Item(Data):
    def patch(self, item_id):
        pp = pprint.PrettyPrinter(indent=4)
        operations = ["increment", "decrement", "set"]

        fields = ["name",
                  "description",
                  "quantity",
                  "unitOfMeasure",
                  "brand",
                  "image",
                  "upc"]

        commands = request.values.to_dict()
        pp.pprint(commands)
        if len(commands) < 1:
            return ["submit changes in the form of", {"op": "val", "args": "val"}], 400

        if len(commands) == 1:
            for key in commands:
                commands = json.loads(key)
                break

        pp.pprint(commands)
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
            if instruction['op'] == 'increment':
                if "amount" not in instruction:
                    return {"no keyword: \"amount\"": instruction}, 400
                else:
                    amount = int(instruction['amount'])
                    return inc(item_id, amount)
            if instruction['op'] == 'decrement':
                if "amount" not in instruction:
                    return {"no keyword: \"amount\"": instruction}, 400
                else:
                    amount = int(instruction['amount'])
                    return dec(item_id, amount)
            if instruction['op'] == 'set':
                to_pass = {key: instruction[key] for key in instruction if key != "op" and key in fields}
                if to_pass == {}:
                    return {"No valid field given": instruction}, 400
                return edit(item_id, **to_pass)

    def delete(self, item_id):
        return delete(item_id), 200

    def get(self, item_id):
        return lookup(item_id)
