import json,sys
from pulp import *

# Import the graph json file
f = open("network.json", "r")
edges = json.load(f)["edges"]
f.close()

# Flow objects contain the connections for a flow network
class flow():
	# Creates an empty dictionary
	def __init__(self):
		self.flow = {}
		
	# Associate a capacity with a source and destination vertex
	# Doesn't have to be a capacity you add here - could be any type of object
	def connect(self,source,dest,capacity):
		self.flow[(source,dest)] = capacity
		
	# Get the object stored between the source and dest vertices
	def get(self,source,dest):
		try:
			return self.flow[(source,dest)]
		except:
			return 0
	
	# Return the data structure as a dictionary
	def get_dict(self):
		return self.flow
		
	# Return an iterator over edges
	def get_list(self):
		return self.flow.iteritems()

myflow = flow()
for edge in edges:
	myflow.connect(edge['u'],edge['v'],edge['c'])



#print myflow.get_dict()
network = flow()
vertices = set()

workstations = set(range(0,6))
server = 19

# create capacity constraints
for edge in edges:
	# Get the vertices
	u, v = edge['u'], edge['v']
	vertices.add(u)
	vertices.add(v)
	# Set the bounds on the edge
	lower = 0
	upper = edge['c']
	# Store a LP variable for this edge
	network.connect(u,v,LpVariable(str(u) + "->" + str(v),lower,upper))
    # Store the antiparallel edge too:
	network.connect(v,u,LpVariable(str(v) + "->" + str(u),lower,upper))

# add a super source to nodes 0 to 5
vertices.add("source")
for src in workstations:
	# note no upper bound is specified!
	network.connect("source",src,LpVariable("source->"+str(src),0))

print network.get_dict()
print vertices
prob = LpProblem("Max flow network transfer problem",LpMaximize)

#TODO Add objective function to problem
#prob += LpSum()

#TODO Add constraints to the problem