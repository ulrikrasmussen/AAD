import json, sys
from pulp import *

prob = LpProblem("dual", LpMinimize)

a = LpVariable("y_{wz}", 0, None)
b = LpVariable("y_{zt}", 0, None)
c = LpVariable("y_{tw}", 0, None)
d = LpVariable("y_{wx}", 0, None)
e = LpVariable("y_{xa}", 0, None)
f = LpVariable("y_{aw}", 0, None)
g = LpVariable("y_{ay}", 0, None)
h = LpVariable("y_{yt}", 0, None)
i = LpVariable("y_{ta}", 0, None)
j = LpVariable("z_{a}", 0, None)
k = LpVariable("z'_{a}", 0, None)
l = LpVariable("z_{x}", 0, None)
m = LpVariable("z'_{x}", 0, None)
n = LpVariable("z_{y}", 0, None)
o = LpVariable("z'_{y}", 0, None)
p = LpVariable("z_{z}", 0, None)
q = LpVariable("z'_{z}", 0, None)
r = LpVariable("z_{w}", 0, None)
s = LpVariable("z'_{w}", 0, None)

prob += lpSum([60*a, 60*b, 60*c, 30*d, 30*e, 30*f, 50*g, 50*h, 50*j])

nlist = []
plist = []
plist += r
nlist += s
prob += lpSum(plist) - lpSum(nlist) >= 1
nlist = []
plist = []
plist += a
plist += p
nlist += q
nlist += r
plist += s
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += b
nlist += p
plist += q
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += c
plist += r
nlist += s
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += d
plist += l
nlist += m
nlist += r
plist += s
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += e
plist += j
nlist += k
nlist += l
plist += m
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += f
nlist += j
plist += k
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += g
nlist += j
plist += k
plist += n
nlist += o
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += h
nlist += n
plist += o
prob += lpSum(plist) - lpSum(nlist) >= 0
nlist = []
plist = []
plist += i
plist += j
nlist += k
prob += lpSum(plist) - lpSum(nlist) >= 0

prob.writeLP("dual.lp")
prob.solve()

print "Status: ", LpStatus[prob.status]
print "Value: ", value(prob.objective)

for var in [a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s]:
    print str(var),":", value(var)
