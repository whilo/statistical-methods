from numpy import *
from math import *

# generates vector of tuples (x, loss)
def vec_loss(loss_func, train, pred):
    return map(lambda x, y: loss_func(x[1], y[1]), train, pred)

def risk_emp(loss_func, train, pred):
    return mean(vec_loss(loss_func,train,pred))

def confidence_term(h,m,delta):
    return sqrt(1/m*(h*(ln(2*m/h+1)+ln(4/delta))))

def bin_loss(y,z) :
	if y*z > 0:
		return 0
	else:
		return 1

# hyperplane classifier
def hyperplane_class(plus,minus):
    c_plus = mean(plus, axis=0)
    c_minus = mean(minus, axis=0)
    c = (c_plus + c_minus)/2
    # defined:
    w = c_plus - c_minus
#    b = pow(norm(c_minus),2)-pow(norm(c_plus),2)
    return lambda x: sign(dot((x-c),w))


plus = [[1, 1], [5, 3], [6, 2]]
minus = [[6, 11], [7, 12], [8, 10]]
print "Plus:"
print plus
print "Minus:"
print minus 


pred_plus = map(lambda x: [x[0],hyperplane_class(plus,minus)(x[0])], plus)
pred_minus = map(lambda x: [x[0],hyperplane_class(plus,minus)(x[0])], minus)
print "Prediction for plus:"
print pred_plus
print "Prediction for minus:"
print pred_minus
print "Binary loss:"
print "Plus: "
print vec_loss(bin_loss, map(lambda x: [x[0],1], plus), pred_plus)
print "Minus: "
print vec_loss(bin_loss, map(lambda x: [x[0],-1], minus), pred_minus)
print
print "Empirical risk:"
print risk_emp(bin_loss, map(lambda x: [x[0],1], plus), pred_plus)
print risk_emp(bin_loss, map(lambda x: [x[0],-1], minus), pred_minus)

print "Kapazitätsterm:"
print confidence_term(10,5,0.5)

