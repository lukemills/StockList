from bson import ObjectId

from database import generic_delete
from database.data import generic_lookup, generic_edit, child

c = "parents"


def delete(name):
    # TODO delete self from user
    # TODO delete associated children lists
    d = generic_delete("children", ObjectId(name))
    if d == 1:
        return {"deleted": d}, 200
    else:
        return {"deleted": d}, 400


def lookup(object_id):
    rv, n = generic_lookup(object_id, c)
    things = ['outstock', 'shopstock', 'instock']
    for t in things:
        rv[t] = child.lookup(rv[t])[0]
    return rv, n


def edit(item_id, **kwargs):
    return generic_edit(item_id, c, **kwargs)
