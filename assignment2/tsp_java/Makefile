JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) -cp ./src:lpsolve55j.jar:ProGAL.jar $(JFLAGS) $*.java

CLASSES = \
		src/edu/aa12/BnBNode.java \
		src/edu/aa12/BranchAndBound_TSP.java \
		src/edu/aa12/DisjointSet.java \
		src/edu/aa12/Edge.java \
		src/edu/aa12/Graph.java \
		src/edu/aa12/Instance1.java \
		src/edu/aa12/Instance2.java \
		src/edu/aa12/Instance3.java \
		src/edu/aa12/Kruskal.java \
		src/edu/aa12/lptest.java \
		src/edu/aa12/MainMethods.java \
		src/edu/aa12/OneTree.java \
		src/edu/aa12/OneTreeBNB.java \
		src/edu/aa12/Visualization.java

default: classes

classes: $(CLASSES:.java=.class)

run: classes
	java -cp src edu.aa12.MainMethods

onetree: classes
	java -cp src edu.aa12.OneTree

clean:
		$(RM) src/edu/aa12/*.class