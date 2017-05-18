import pymongo
from bson import ObjectId
from flask_restful import Resource

database = "production"


def generic_delete(collection_name, given_id):
    given_id = ObjectId(given_id)
    with pymongo.MongoClient("localhost") as client:
        collection = client[database][collection_name]
        return collection.delete_one({'_id': given_id}).deleted_count()


class Document(Resource):
    @staticmethod
    def create(name):
        raise NotImplementedError

    @staticmethod
    def delete(name):
        raise NotImplementedError


class GenericError(Exception):
    def __init__(self):
        self.message = "This is a Generic Error, this should be overwritten"
