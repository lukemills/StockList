import pymongo
from database import database, Document, GenericError
from pymongo import MongoClient
from pymongo.cursor import Cursor


class ListNotFound(GenericError):
    def __init__(self):
        pass


class List(Document):
    @staticmethod
    def create(name):
        # todo return the mongo id of the list
        with pymongo.MongoClient("localhost") as client:
            collection = client[database]["lists"]
            list_doc = {
                "list_name": name,
                "items": []
            }

            id_insert = collection.insert_one(list_doc)
            id_insert = id_insert.inserted_id
            return id_insert

    @staticmethod
    def internal_find(client: MongoClient, name: str) -> Cursor:
        collection = client[database]["lists"]
        search = collection.find({"list_name": name})
        return search

    @staticmethod
    def internal_find_one(client: MongoClient, name: str):
        c = List.internal_find(client, name)
        return c.next()

    @staticmethod
    def get_name(list_id) -> str:
        with pymongo.MongoClient("localhost") as client:
            collection = client[database]["lists"]
            search = collection.find({"_id": list_id})
            search = search.next()
            return search['list_name']
