import json,sys
from pulp import *

print_debug = True
simple = False


# Import the graph json file
if simple:
	filename = "simple.json"
else:
	filename = "network.json"
f = open(filename, "r")
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


#print myflow.get_dict()
network = flow()
cost_network = flow()
vertices = set()

# Identify the vertices which are special:
workstations = set(range(0,6))
external_relays = set(range(20,27))
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
	network.connect(u,v,LpVariable(str(u) + "_to_" + str(v),lower,upper))
    # Store the antiparallel edge too:
	network.connect(v,u,LpVariable(str(v) + "_to_" + str(u),lower,upper))
	# Store a cost constraint
	if u in external_relays or v in external_relays:
		cost_network.connect(u,v,LpVariable("Expensive_" + str(u) + "_to_" + str(v),1,1))
		cost_network.connect(v,u,LpVariable("Expensive_" + str(v) + "_to_" + str(u),1,1))
	else: # All internal connections must be free (zero cost)
		cost_network.connect(u,v,LpVariable("Free_" + str(u) + "_to_" + str(v),0,0))
		cost_network.connect(v,u,LpVariable("Free_" + str(v) + "_to_" + str(u),0,0))

# add a super source to workstations
if not simple:
	vertices.add("source")
	for src in workstations:
		# note no upper bound is specified!
		network.connect("source",src,LpVariable("source_to_"+str(src),0,None))

# Check it's all in the data structure
if (print_debug):
	print network.get_dict()
	print cost_network.get_dict()
	print vertices

# Begin solving using Pulp
prob = LpProblem("Max flow network transfer problem",LpMaximize)
prob_cost = LpProblem("Minimum cost flow network transfer problem",LpMinimize)
variables = network.get_dict()
cost_variables = cost_network.get_dict()


# Collect all the flow out of the source - note nothing flows in
source_connected = []
for var in variables:
	if var[0] == "source":
		source_connected.append(variables[var])

# Add the objective functions
prob 		+= lpSum(source_connected), "Flow out of super source"
prob_cost	+= lpSum(), "Minimise the overall cost"

# Add constraints on flow conservation
for vertex in vertices:
	if vertex == server or vertex == "source":
		continue
	# find all edges in:
	flow_in  = []
	flow_out = []
	for var in variables:
		if var[1] == vertex:
			flow_in.append(variables[var])
		elif var[0] == vertex:
			flow_out.append(variables[var])
	prob += lpSum(flow_in) - lpSum(flow_out) == 0, "Flow conservation for " + str(vertex)

prob.writeLP("maxflow.lp")
prob.solve()

print "Max flow Status: ", LpStatus[prob.status]
print "Max flow Value: ", value(prob.objective)

# Print out the actual flows:
if print_debug:
	for var in variables:
		print value(variables[var])