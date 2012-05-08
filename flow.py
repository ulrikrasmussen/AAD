from pulp import *
from pulp import CPLEX_CMD

class flow():
	def __init__(self):
		self.flow = {}
		
	def connect(self,source,dest,capacity):
		self.flow[(source,dest)] = capacity
	
	def get(self,source,dest):
		try:
			return self.flow[(source,dest)]
		except:
			return 0
	
	def get_dict(self):
		return self.flow
		
	def get_list(self):
		return self.flow.iteritems()