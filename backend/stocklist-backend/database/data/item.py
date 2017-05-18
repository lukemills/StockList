import pymongo
from bson import ObjectId

from database import database, generic_delete
from database.data import generic_lookup, generic_edit

c = "items"


def lookup(item_id):
    return generic_lookup(item_id, c)


def delete(name):
    # TODO also delete self from child list

    d = generic_delete("children", ObjectId(name))
    if d == 1:
        return {"deleted": d}, 200
    else:
        return {"deleted": d}, 400


def edit(item_id, **kwargs):
    return generic_edit(item_id, c, **kwargs)


def mod(item_id, amt, direction):
    item_id = ObjectId(item_id)
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]
        item = collection.find_one({"_id": item_id})
        if item is None:
            return ["Bad user"], 400
        else:
            if direction == "pos":
                item['quantity'] += amt
            elif direction == "neg":
                item['quantity'] -= amt
            collection.save(item)
            print("item is now", item)
            return item, 400


def inc(item_id, amt):
    return mod(item_id, int(amt), "pos")


def dec(item_id, amt):
    return mod(item_id, int(amt), "neg")
