import pymongo

from database import database

c = "children"


def create(list_name):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        list_doc = {
            "list_name": list_name,
            "items": []
        }

        id_insert = collection.insert_one(list_doc)
        id_insert = id_insert.inserted_id
        return str(id_insert)


def list_children():
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        find_ = []
        for parent in collection.find({}):
            x = {'list_name': parent['list_name'], 'id': str(parent['_id'])}

            find_.append(x)

        return find_
