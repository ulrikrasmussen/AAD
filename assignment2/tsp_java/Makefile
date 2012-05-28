JFLAGS = -g
JC = javac
CLASSPATH = ./src:lpsolve55j.jar:ProGAL.jar
.SUFFIXES: .java .class
.java.class:
		$(JC) -cp $(CLASSPATH) $(JFLAGS) $*.java

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
	java -cp $(CLASSPATH) edu.aa12.MainMethods

onetree: classes
	java -cp $(CLASSPATH) edu.aa12.OneTree

clean:
		$(RM) src/edu/aa12/*.class