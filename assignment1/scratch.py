#scratch file
import json,sys

f = open("network.json", "r")
edges = json.load(f)["edges"]
f.close()

for edge in edges:
    print "Edge from ", edge['u'], " to ", edge['v'], ": ", edge['c']