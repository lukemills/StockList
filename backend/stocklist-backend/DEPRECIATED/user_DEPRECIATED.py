import pymongo
from DEPRECIATED.list_DEPRECIATED import List
from database import database, generic_delete, GenericError
from pymongo import MongoClient
from pymongo.cursor import Cursor


class UserNotFound(GenericError):
    def __init__(self):
        pass


def internal_get_list(username):
    with pymongo.MongoClient("localhost") as client:

        user = internal_find_one(client, username)

        if 'lists' not in user:
            return {}
        else:
            return [list_key for list_key in user['lists']]


def add_list(username, list_name):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database]["users"]
        user = internal_find_one(client, username)
        user_list = List.create(list_name)

        if 'lists' not in user:
            user['lists'] = []
        user['lists'].append(user_list)
        collection.save(user)


def change_username(username, new_name):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database]["users"]

        if not find(username):
            # todo raise an error
            pass
        else:
            # we found the user, we can change the name
            user = internal_find(client, username).next()
            print("messing with", user)
            user['username'] = new_name
            collection.save(user)


def internal_find_one(client: MongoClient, username: str):
    c = internal_find(client, username)
    try:
        return c.next()
    except StopIteration:
        raise UserNotFound


def internal_find(client: MongoClient, username: str) -> Cursor:
    collection = client[database]["users"]
    search = collection.find({"username": username})
    return search


def find(username):
    # todo should return if a user exists or not
    with pymongo.MongoClient("localhost") as client:
        search = internal_find(client, username)

    return search.count() > 0


def create(username, first_name="Jane", last_name="Doe"):
    with pymongo.MongoClient("localhost") as client:
        collection = client[database]["users"]

        user_doc = {
            "username": username,
            "firstname": first_name,
            "surname": last_name,
            "lists": []
        }

        id_insert = collection.insert_one(user_doc)
        id_insert = id_insert.inserted_id
        print("Successfully inserted document, objectid: %s" % id_insert)
        return str(id_insert)


def get_list(username):
    ids = internal_get_list(username)
    if not ids:
        return []
    return list(map(List.get_name, ids))


def list_users():
    with pymongo.MongoClient("localhost") as client:
        collection = client[database]["users"]

        find_ = []
        for user in collection.find({}):
            x = {'username': user['username'], 'id': str(user['_id'])}

            find_.append(x)

        # page_sanitized = json.loads(json_util.dumps(find_))
        # print(page_sanitized)

        return find_


def delete(username):
    generic_delete("users", username)
