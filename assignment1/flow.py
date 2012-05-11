import json,sys
from pulp import *
import graph

# Do you want to print out lots of edge details?
print_debug = False

# Import the graph json file
filename = "network.json"
f = open(filename, "r")
edges = json.load(f)["edges"]
f.close()

# Create some data structures for storing edge variables
network = graph.graph()
cost_network = graph.graph()

# Identify the vertices using sets
vertices = set()
workstations = set(range(0,6))
external_relays = set(range(20,27))
server = 19

# create capacity constraints
for edge in edges:
	# Deal with the vertices
	u, v, capacity = edge['u'], edge['v'], edge['c']
	vertices.add(u)
	vertices.add(v)
	# Set the bounds on the edge
	lower, upper = 0, capacity
	# Store a LP variable for this edge
	network.connect(u,v,LpVariable(str(u) + "_to_" + str(v),lower,upper))
    # Store the antiparallel edge too:
	network.connect(v,u,LpVariable(str(v) + "_to_" + str(u),lower,upper))
	# Store a cost constraint if the edge leaves the internal network
	if (u in external_relays and not (v in external_relays)) or \
       (v in external_relays and not (u in external_relays)):
		cost_network.connect(u,v,1)
		cost_network.connect(v,u,1)
	else: # All internal connections must be free (zero cost)
		cost_network.connect(u,v,0)
		cost_network.connect(v,u,0)

# add a super source to workstations
vertices.add("supersource")
for src in workstations:
	# note no upper bound on capacity is specified!
	network.connect("supersource",src,LpVariable("supersource_to_"+str(src),0,None))
	cost_network.connect("supersource",src,0)

# Check it's all in the data structure
if (print_debug):
	print "Capacity constraints per edge: "
	print network.get_dict()
	print "Costs per edge:"
	print cost_network.get_dict()
	print "Vertices: "
	print vertices

# Reformat graphs as dictionaries: 
variables 	= network.get_dict()
costs 		= cost_network.get_dict()
# Collect variables for the objective functions
source_connected 	= [] # maximise the flow from the supersource
cost_minimisation 	= [] # minimise the network transfer costs

for edge in variables:
	# Add the source-connected edges for flow maximisation
	if edge[0] == "supersource":
		source_connected.append(variables[edge])
	# Collect the cost minimisation term for every edge:
	cost_minimisation.append(costs[edge]*variables[edge])

# Begin problem definition using Pulp
prob 		= LpProblem("Max flow network transfer problem",LpMaximize)
prob_cost 	= LpProblem("Minimum cost flow network transfer problem",LpMinimize)

# Add the objective functions
prob 		+= lpSum(source_connected), "Flow out of super source"
prob_cost	+= lpSum(cost_minimisation), "Network transfer costs"
#TODO try the cost minimisation as a LpAffineExpression

# Add constraints on flow conservation
for vertex in vertices:
	# Ignore the flow conservation for the server or sink
	if vertex == server or vertex == "supersource":
		continue
	# find all edges inward and outward:
	flow_in  = []
	flow_out = []
	for edge in variables:
		if edge[1] == vertex:
			flow_in.append(variables[edge])
		elif edge[0] == vertex:
			flow_out.append(variables[edge])
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

print "The following costs were incurred by using external connections in the optimal solution"
for edge in costs:

	if costs[edge] == 1: # only include the costly edges
		optimal_cost = value(costs[edge])*value(variables[edge])
		optimal_flow = value(variables[edge])
		print edge," cost: ",optimal_cost,"oere. Flow:",optimal_flow

# Print out the optimal flows:
if print_debug:
	for var in variables:
		print str(var),":", value(variables[var])
