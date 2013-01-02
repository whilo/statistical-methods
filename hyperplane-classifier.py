from __future__ import division
from numpy import *
from numpy.linalg import *
from math import *
import matplotlib
from matplotlib import pyplot as plt

##########################################################
#Aufgabe 7.2: Hyperplane Classifier

def pnf(x,a,w) :
    return float(dot(x,(w/norm(w)))-dot(a,(w/norm(w))));

def classifier(input, o, r) :
	return dot(input, o) - r

def margin(x,y, rho) :
    tmp = []
    result = []
    for entry in x :
        tmp.append(dot(entry, omega))
    for i in range(len(y)) :
        result.append((y[i]/norm(omega))*(tmp[i]-rho))
    return result

def classify(x, o, r):
    result = []
    for entry in x :
	    result.append(classifier(entry, omega, rho))
    return result

    

omega = [3,4]
point = [3,4]
x = [[6,1],[1,1],[-1,4],[8,3],[2,7]]
y = [-1,-1,-1,1,1]
r = abs(pnf([0,0], point, omega))
rho = r*norm(omega)


print "Aufgabe 7.2: Hyperplane Classifier"
print "r =", r
print "rho =", rho
print "m =", margin(x,y,rho)
print "M =", amin(margin(x,y,rho))
print "Classifier f =", classify(x,omega,rho)
print "Plotting the stuff:"

#Plot
plt.plot(map(lambda z: z[0], x),map(lambda z: z[1], x), 'bo')
t=arange(-2.,10.,0.2)
plt.plot(t, -3/4*t+25/4)
plt.axis([-2,10,-2,10])
#plt.show()

print "Eintragung falsch klassifizierter Punkt [5,4]"
xfalse = [5,4]
plt.plot(map(lambda z: z[0], x),map(lambda z: z[1], x), 'bo')
plt.plot(xfalse[0], xfalse[1], 'ro')
t=arange(-2.,10.,0.2)
plt.plot(t, 4/3*t)
plt.axis([-2,10,-2,10])
plt.show()
x.append(xfalse)
y.append(-1)
print "m =", margin(x,y,rho)
print "Classifier f =", classify(x,omega, rho) 


