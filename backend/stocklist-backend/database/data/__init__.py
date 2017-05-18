import pymongo
from bson import ObjectId

from database import database


def generic_lookup(object_id, collection_name):
    object_id = ObjectId(object_id)
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][collection_name]
        item = collection.find_one({"_id": object_id})
        if item is None:
            return {"Not Found"}, 404
        return item, 200


def generic_edit(object_id, collection_type, **kwargs):
    if "_id" in kwargs:
        # Preventing Colin from breaking the object id
        del kwargs["_id"]
    object_id = ObjectId(object_id)
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][collection_type]
        obj = collection.find_one({"_id": object_id})
        if obj is None:
            return ["Bad" + str(collection_type)], 400
        else:
            for field in kwargs:
                obj[field] = kwargs[field]
            collection.save(obj)
            return obj, 200
