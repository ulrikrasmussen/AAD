import json,sys
from pulp import *

print_debug = False


# Import the graph json file
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
		cost_network.connect(u,v,1)
		cost_network.connect(v,u,1)
	else: # All internal connections must be free (zero cost)
		cost_network.connect(u,v,0)
		cost_network.connect(v,u,0)

# add a super source to workstations
vertices.add("source")
for src in workstations:
	# note no upper bound is specified!
	network.connect("source",src,LpVariable("source_to_"+str(src),0,None))
	cost_network.connect("source",src,0)

# Check it's all in the data structure
if (print_debug):
	print network.get_dict()
	print cost_network.get_dict()
	print vertices

# Begin solving using Pulp
prob 		= LpProblem("Max flow network transfer problem",LpMaximize)
prob_cost 	= LpProblem("Minimum cost flow network transfer problem",LpMinimize)
variables 	= network.get_dict()
costs 		= cost_network.get_dict()
dupvariables= network.get_dict()


# Collect variables for the objective functions
source_connected 	= [] # maximise the flow from the supersource
cost_minimisation 	= [] # minimise the network transfer costs

for var in variables:
	# Add the source-connected edges for flow maximisation
	if var[0] == "source":
		source_connected.append(variables[var])
	# Collect the cost minimisation term for every edge:
	cost_minimisation.append(costs[var]*variables[var])

# Add the objective functions
prob 		+= lpSum(source_connected), "Flow out of super source"
prob_cost	+= lpSum(cost_minimisation), "Network transfer costs"

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
	flow_conservation = lpSum(flow_in) - lpSum(flow_out) == 0, "Flow conservation for " + str(vertex)
	prob 		+= flow_conservation
	prob_cost	+= flow_conservation

# Solve the max flow problem
prob.writeLP("maxflow.lp")
prob.solve()

print "Max flow Status: ", LpStatus[prob.status]
print "Max flow Value: ", value(prob.objective)


# add the optimal max flow as a constraint on the minimal cost problem:
prob_cost += lpSum(source_connected) == value(prob.objective), "Flow must be the maximum"
prob_cost.writeLP("mincost.lp")
prob_cost.solve()


print "Min cost Status: ", LpStatus[prob_cost.status]
print "Min cost Value: ", value(prob_cost.objective)

print "The following costs were incurreed by using external connections in the optimal solution"
for cost in costs:
	if not costs[cost] == 0: # only include the costly edges
		optimal_cost = value(costs[cost])*value(variables[cost])
		print cost,"optimal cost: ",value(costs[cost])*value(variables[cost])

# Print out the optimal flows:
if print_debug:
	for var in variables:
		print str(var),":", value(variables[var])