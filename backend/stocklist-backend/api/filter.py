from flask_restful import Resource

from database.filter import do_the_things


class Filter(Resource):
    @staticmethod
    def get(user_id, list_id):
        return do_the_things(user_id, list_id), 200
