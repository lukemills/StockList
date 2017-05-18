import pymongo
from bson import ObjectId

from database import database
from database.collection import child

c = "parents"


def create(list_name):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]
        list_doc = {
            "list_name": list_name,
            "instock": ObjectId(child.create("InStock")),
            "outstock": ObjectId(child.create("OutStock")),
            "shopstock": ObjectId(child.create("ShopStock"))
        }

        id_insert = collection.insert_one(list_doc)
        id_insert = id_insert.inserted_id
        return str(id_insert)


def list_parents():
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        find_ = []
        for parent in collection.find({}):
            x = {'list_name': parent['list_name'], 'id': str(parent['_id'])}

            find_.append(x)

        return find_
