import json,sys

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
		
	# Return the list of edges
	def get_list(self):
		return self.flow.iteritems()