from bson import ObjectId

from database import generic_delete
from database.data import generic_edit, generic_lookup, parent

c = "users"


def lookup(user_id):
    rv, n = generic_lookup(user_id, c)
    rv['lists'] = (list(map(lambda i: parent.lookup(i)[0], rv['lists'])))

    return rv, n


def delete(name):
    # TODO delete associated parents
    object_id = ObjectId(name)
    d = generic_delete("children", object_id)
    if d == 1:
        return {"deleted": d}, 200
    else:
        return {"deleted": d}, 400


def edit(child_id, **kwargs):
    return generic_edit(child_id, c, **kwargs)
