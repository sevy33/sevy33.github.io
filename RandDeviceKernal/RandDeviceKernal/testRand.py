#!/usr/bin/env python3

f = open("/dev/myRand","r+", encoding="latin1")
arr = f.read(5)
print(arr)
f.write("0123456789")
arr = f.read(5)
print(arr)
f.write("0123456789")
arr = f.read(5)
print(arr)
f.close()
