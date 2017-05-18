from bson import ObjectId

from database import generic_delete
from database.data import generic_lookup, generic_edit, item

c = "children"


def delete(child_name):
    # TODO also delete self from parent list
    # TODO also delete associated item data
    d = generic_delete("children", ObjectId(child_name))
    if d == 1:
        return {"deleted": d}, 200
    else:
        return {"deleted": d}, 400


def lookup(child_id):
    rv, n = generic_lookup(child_id, c)
    rv['items'] = (list(map(lambda i: item.lookup(item_id=i)[0], rv['items'])))
    return rv, n


def edit(child_id, **kwargs):
    return generic_edit(child_id, c, **kwargs)
