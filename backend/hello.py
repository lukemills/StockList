from subprocess import call #Can run CLI commands //call
import os #Allows us to change directories //os.chdir
from  os import path

call(["pwd"])
os.chdir("/home/colin/Desktop")
call(["ls", "-l"])

if os.path.isfile("/home/colin/scripts/stockListWebUpdate/hello.py"):
	print "\nTrue"
else: 
	print "\nFalse"
