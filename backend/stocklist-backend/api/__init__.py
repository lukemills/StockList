from flask_restful import Resource


class Data(Resource):
    def get(self, object_id):
        raise NotImplementedError

    # noinspection PyUnusedLocal
    @staticmethod
    def post(object_id):
        return {}, 501

    def delete(self, object_id):
        raise NotImplementedError

    def patch(self, object_id):
        raise NotImplementedError


class Collection(Resource):
    def get(self):
        raise NotImplementedError

    def post(self):
        raise NotImplementedError

    @staticmethod
    def delete():
        return {}, 501

    @staticmethod
    def patch():
        return {}, 501
