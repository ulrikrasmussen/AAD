# Flow objects contain the connections for a flow network
class graph():
    # Creates an empty dictionary
    def __init__(self):
        self.graph = {}

    # Associate a capacity with a source and destination vertex
    # Doesn't have to be a capacity you add here - could be any type of object
    def connect(self,source,dest,capacity):
        self.graph[(source,dest)] = capacity

    # Get the object stored between the source and dest vertices
    def get(self,source,dest):
        try:
            return self.graph[(source,dest)]
        except:
            return 0

    # Return the data structure as a dictionary
    def get_dict(self):
        return self.graph

    # Return an iterator over edges
    def get_list(self):
        return self.graph.iteritems()

