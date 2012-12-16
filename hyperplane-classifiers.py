from __future__ import division
from numpy import *
from numpy.linalg import *
from math import *
import matplotlib
import matplotlib.pyplot as plt

# hyperplane classifier
def myhyperplane_class(x):
    return sign( margin(x,1) )

def margin(x,y):
    # defined:
    w = [3,4]
    r = dot(w,p)/norm(w, ord=2)
    rho = r*norm(w, ord=2)
    return (y/norm(w, ord=2))*( dot(x,w) - rho )

minus = [[6,1],[1,1],[-1,4]]
plus = [[8,3],[2,7]]
p = [3,4]
w = [3,4]

print "7.2 Hyperplane classifier"
print "a)"
r = dot(w,p)/norm(w, ord=2)
print "r (from point in plane p: {0}): {1}".format(p,r)

rho = r*norm(w, ord=1)
print "rho: {0}".format(rho)

m_minus = map(lambda x: [x,margin(x,-1)], minus)
m_plus = map(lambda x: [x,margin(x,1)], plus)
m_i = m_minus+m_plus
print "m_i [point,margin]: {0}".format(m_i)

print "b) Wie gross ist M?"
print min(map( lambda x: abs(x[1]), m_i ))
print "c) Werden die Trainingsdaten richtig klassifiziert?"
print map(lambda x: [x, myhyperplane_class(x)], minus + plus)

print "d) Zeichnung"
plt.plot(map(lambda x: x[0], minus+plus), map(lambda x: x[1], minus+plus ), 'ro' )
t = arange(-2.,10.,0.2)
plt.plot(t, -4/3*t+8)
plt.axis([-2,10,0,10])
plt.show()

print "e) Nehme falsch klassifizierten Punkt x_6 [[4,2],-1]"
print "Margin m_6 ist {0} mit -sign(-1)".format(margin([4,2],-1))
