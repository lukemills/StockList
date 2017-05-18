from time import sleep

import gridfs
import httplib2
import pymongo
from apiclient import discovery
from gridfs.errors import FileExists
from oauth2client import tools
from sshtunnel import SSHTunnelForwarder

try:
    # noinspection PyUnresolvedReferences
    import argparse

    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None


def main():
    """Shows basic usage of the Gmail API.

    Creates a Gmail API service object and outputs a list of label names
    of the user's Gmail account.
    """
    http = credentials.authorize(httplib2.Http())
    service = discovery.build('gmail', 'v1', http=http)

    local_address = '0.0.0.0'
    port = 10022
    # noinspection PyUnusedLocal
    with SSHTunnelForwarder(
            ("198.199.77.214", 22),
            ssh_username="sysadmin",
            ssh_pkey="/home/cmennen/.ssh/hydra_rsa",
            remote_bind_address=("localhost", 27017),
            local_bind_address=(local_address, port)
    ) as tunnel:
        sleep(1)

        with pymongo.MongoClient(local_address, port=port) as client:
            # code starts here
            db = client['processing']
            filedb = client['files']
            message_collection = db['needs_processing']
            message_cursor = message_collection.find({})

            for message in message_cursor:
                lab_number = message['lab']
                tars = gridfs.GridFS(filedb, collection='lab' + lab_number)
                # print("working with", message['filename'])
                if (tars.find({"_id": message['filename']}).count()) > 0:
                    continue
                attachment = GetAttachments(service, 'me', message['_id'])
                if len(attachment) is not 1:
                    print("Wrong number of attatchments")
                    continue

                attachment = attachment[0]

                try:
                    tars.put(attachment, _id=message['filename'])
                    # print("inserted", message['filename'])
                except FileExists:
                    pass


if __name__ == '__main__':
    main()
