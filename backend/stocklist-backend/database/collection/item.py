import pymongo

from database import database

# Collection name we push into
c = "items"


def create(name="PenisPills", description="Jane wants jim to have a big dick", quantity=100, unit_of_measure="count",
           brand="Off Brand", image="penis.gif", upc="123456790"):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        user_doc = {
            "name": name,
            "description": description,
            "quantity": int(quantity),
            "unitOfMeasure": unit_of_measure,
            "brand": brand,
            "image": image,
            "upc": upc
        }

        id_insert = collection.insert_one(user_doc)
        id_insert = id_insert.inserted_id
        return str(id_insert)


def list_items():
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        find_ = []
        for item in collection.find({}):
            x = {'name': item['name'], 'id': str(item['_id'])}

            find_.append(x)

        return find_
