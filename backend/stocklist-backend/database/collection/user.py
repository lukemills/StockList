import pymongo

from database import database

c = "users"


def create(username="jon123", first_name="Jane", last_name="Doe"):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        user_doc = {
            "username": username,
            "first_name": first_name,
            "last_name": last_name,
            "lists": []
        }

        id_insert = collection.insert_one(user_doc)
        id_insert = id_insert.inserted_id
        return str(id_insert)


def list_users():
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][c]

        find_ = []
        for user in collection.find({}):
            x = {'username': user['username'], 'id': str(user['_id'])}

            find_.append(x)

        # page_sanitized = json.loads(json_util.dumps(find_))
        # print(page_sanitized)

        return find_
