#!/home/cmennen/.virtualenvs/grading/bin/python


import subprocess
from time import sleep

import gridfs
import os
import pymongo
import shutil
import tarfile
import tqdm
from graders.lab5 import *
from sshtunnel import SSHTunnelForwarder

local_address = '0.0.0.0'
port = 10022
path_join = os.path.join("..", "..", "..", "..", "..", "scratch", "cmennen")

inspect = False

with SSHTunnelForwarder(
        ("198.199.77.214", 22),
        ssh_username="sysadmin",
        ssh_pkey="/home/cmennen/.ssh/hydra_rsa",
        remote_bind_address=("localhost", 27017),
        local_bind_address=(local_address, port)
) as _:
    sleep(1)

    join = os.path.join(path_join, lab)
    try:
        os.makedirs(join)
    except OSError:
        pass
    os.chdir(join)
    FNULL = open(os.devnull, 'w')

    with pymongo.MongoClient(local_address, port=port) as client:
        to_inspect = 0
        processed = client['processed'][lab]
        grades = client['grades'][lab]
        file_db = client['files']
        tars = gridfs.GridFS(file_db, collection=lab)

        grade_cursor = grades.find({
            "correct": {"$exists": False}
        })

        for needs_grading in grade_cursor:
            student = processed.find_one({
                "_id": needs_grading["_id"]
            })

            # Do the tar stuff

            tar_filename = student['submissions'][0]['filename']
            grid_out = tars.find_one({
                '_id': tar_filename
            }, no_cursor_timeout=True)

            with tarfile.open(mode="r:gz", fileobj=grid_out) as tar:
                assert isinstance(tar, tarfile.TarFile)
                def is_within_directory(directory, target):
                    
                    abs_directory = os.path.abspath(directory)
                    abs_target = os.path.abspath(target)
                
                    prefix = os.path.commonprefix([abs_directory, abs_target])
                    
                    return prefix == abs_directory
                
                def safe_extract(tar, path=".", members=None, *, numeric_owner=False):
                
                    for member in tar.getmembers():
                        member_path = os.path.join(path, member.name)
                        if not is_within_directory(path, member_path):
                            raise Exception("Attempted Path Traversal in Tar File")
                
                    tar.extractall(path, members, numeric_owner=numeric_owner) 
                    
                
                safe_extract(tar, path="..")
            try:
                subprocess.check_call(["make"], stdout=FNULL, stderr=FNULL)
            except subprocess.CalledProcessError:
                tqdm.tqdm.write("compilation error " + student['_id'])
                needs_grading['correct'] = 0
                if "special_comment" not in needs_grading:
                    needs_grading['special_comment'] = "did not compile"
                else:
                    needs_grading['special_comment'] += ", did not compile"

                if inspect:
                    while "y" not in raw_input("Ready to continue? "):
                        pass
                else:
                    to_inspect += 1
                    subprocess.call(["chmod", '-R', '700', '.'])

                    shutil.rmtree(student_path)
                    pbar.update(1)
                    grades.save(needs_grading)
                    continue

            output = subprocess.check_output(["/home/plank/cs360/labs/" + lab + "/gradeall"])

            n_correct = output.count('is correct.')

            needs_grading['correct'] = int(n_correct)
            grades.save(needs_grading)

            subprocess.call(["chmod", '-R', '700', '.'])

            shutil.rmtree(student_path)
            pbar.update(1)

            # break
        print("have", to_inspect, "to hand grade")
