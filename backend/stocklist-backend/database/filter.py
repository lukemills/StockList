import pymongo
from bson import ObjectId

from database import database


def do_the_things(user_id, list_id):
    user_id = ObjectId(user_id)
    list_id = ObjectId(list_id)

    with pymongo.MongoClient("localhost") as client:
        user_collection = client[database]["users"]

        user = user_collection.find_one({"_id": user_id})

        if user is None:
            return ["user does not exist", user_id], 400

        if list_id not in user['lists']:
            return ["user does not have list with id", list_id], 400

        found_list = client[database]["parents"].find_one({"_id": list_id})

        if found_list is None:
            return ["list does not exist", list_id], 400

        shop_stock = client[database]["children"].find_one({"_id": found_list['shopstock']})
        in_stock = client[database]["children"].find_one({"_id": found_list['instock']})
        out_stock = client[database]["children"].find_one({"_id": found_list['outstock']})

        for stock in [shop_stock, in_stock, out_stock]:
            print(stock)
            a = [client[database]["items"].find_one({"_id": item_id}) for item_id in stock['items']]
            print(a)
            # todo finish
