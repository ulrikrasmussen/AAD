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
		self.flow[(dest,source)] = capacity
	
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
# create capacity constraints
for edge in edges:
	# need variable bound
	u, v = edge['u'], edge['v']
	lower = 0
	upper = edge['c']
	varname = str(u) + "to" + str(v)
	network.connect(u,v,LpVariable(varname,lower,upper))
	#print "%s  -> %s, cap=%s" % (u,v, edge['c'])        

print network.get_dict()

prob = LpProblem("Max flow network transfer problem",LpMaximize)

#TODO Add objective function to problem
#prob += LpSum()

#TODO Add constraints to the problem