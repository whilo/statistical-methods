# -*- coding: utf-8 -*-

#Learning project Algorithmus
######################################################################

from numpy import dot
from math import *

# Function name nails it
def readFile(path) :
   return [line.strip() for line in open(path)]

# Define words to be searched in the corpus
def categories() :
    categoryList = []
    categoryList.append('Verband')
    categoryList.append('Struktur')
    categoryList.append('Lehre')
    categoryList.append('Körper')
    categoryList.append('Teilgebiet')
    categoryList.append('Person')
    categoryList.append('Krankheit')
    categoryList.append('Medizin')
    categoryList.append('Sinne')
    return categoryList

#Count the given words in a given text
def scrape(inputText, categories) :
    features = []
    for entry in categories :
        count = inputText.count(entry);
        if(count != -1) :
            features.append(count)
        else :
            features.append(0)
    return features;

#Given perceptron algorithm
def perceptron(x_in,y_in,rho_init,omega_init, alpha) :
    errors = False
    rho = rho_init
    omega = omega_init
    x = x_in
    y = y_in
    while not errors :
        for i in range(len(y)) :
            if (y[i]*(dot(omega,x[i]) + rho)) <= 0 :
                omega = map(sum, zip(omega,map(lambda z: alpha*y[i]*z, x[i])))
                rho = rho + alpha*y[i]
                errors = True
                print 'rho = {}, omega = {}'.format(rho, omega)

# Initializing the stuff
rawData = readFile('/home/konny/uni/sm/blaetter/corpus.txt')
categories = categories()
omega_init = [1,1,1,1,1,1,1,1,1]
rho_init = 0
alpha = 0.5
y = [1,-1,-1,1,1,1,1,-1,1,-1]
x = []

# Count the words in the rawData
for i in range(len(rawData)) :
    if(rawData[i] != '') :
        x.append(scrape(rawData[i], categories))

# Energize!
perceptron(x,y,rho_init,omega_init, alpha)
