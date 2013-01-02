from numpy import dot
from math import *

###########################################################
#Aufgabe 5.1: Loss for Classification

def classifier(input, o, r) :
	return dot(input, o) - r

def binLoss(y,z) :
	if y*z > 0:
		return 0
	else:
		return 1

def hingeLoss(y,z) :
	if y*z >= 1:
		return 0
	else:
		return 1-y*z

def quadraticHingeLoss(y,z) :
	if float(pow((y*z),2)) >= 1:
		return 0
	else:
		return float(pow((1-y*z),2))

def logisticLoss(y,z) :
	return log(1+exp(-y*z), 2)

x = [[1,1],[2,4],[5,4],[2,2],[0,1]]
y = [1,1,-1,-1,1]
f = []
omega = [1,1]
rho = 3
binary = []
hinge = []
quadraticHinge = []
logistic = []

#classifier berechnen
for entry in x :
	f.append(classifier(entry, omega, rho))

#0-1-Loss
for i in range(len(y)) :
	binary.append(binLoss(y[i], f[i]))

#Hinge Loss
for i in range(len(y)) :
	hinge.append(hingeLoss(y[i], f[i]))

#Quadratic Hinge Loss
for i in range(len(y)) :
	quadraticHinge.append(quadraticHingeLoss(y[i], f[i]))

#Logistic Loss
for i in range(len(y)) :
	logistic.append(logisticLoss(y[i], f[i]))

print '<-------> Aufgabe 5.1 Loss for Classification <------>'
print 'prediction : {}'.format(y)
print 'classifier : {}'.format(f)
print '0-1-Loss : {}'.format(binary)
print 'Hinge Loss : {}'.format(hinge)
print 'Quadratic Hinge Loss : {}'.format(quadraticHinge)
print 'Logistic Loss : {}'.format(logistic)

###########################################################
# Aufgabe 5.2: Loss for Regression

def regression(x) :
	return 0.8*x + 0.5

def absoluteLoss(y,z) :
	return fabs(z-y)	

def squaredLoss(y,z):
	return pow(z-y, 2)

def epsilonInsensitiveLoss(y,z, epsilon) :
	return fabs(z-y)
#	if fabs(z-y) <= epsilon :
#		return 0
#	else :
#		return fabs(z-y)-epsilon
		

x = [1,2,5,2,0]
y = [1,4,4,2,1]
f = []
absolute = []
squared = []
insensitive = []
epsilon = 1

#Regressionsgerade berechnen
for entry in x:
	f.append(regression(entry))

#Absolute Loss
for i in range(len(y)) :
	absolute.append(absoluteLoss(y[i], f[i]))

#Squared Loss
for i in range(len(y)) :
	squared.append(squaredLoss(y[i], f[i]))

#Epsilon-insensitive Loss
for i in range(len(y)) :
	insensitive.append(epsilonInsensitiveLoss(y[i], f[i],epsilon))



print "<-------> Aufgabe 5.2 Loss for Regression <------>"
print "Absolute Loss : {}".format(absolute)
print "Squared Loss : {}".format(squared)
print "Epsilon-insensitive Loss (epsilon = {}): {}".format(epsilon, insensitive)

###########################################################

# Aufgabe 5.3
# 
