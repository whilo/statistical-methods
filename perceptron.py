# -*- coding: utf-8 -*-

# Perceptron Algorithm: Implementation of Gradient Descent
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

# Count the given words in a given text
def scrape(inputText, categories) :
    features = []
    for entry in categories :
        count = inputText.count(entry + ' ');
        count += inputText.count(entry + '.')
        count += inputText.count(entry + ',')
        count += inputText.count(entry + '!')
        count += inputText.count(entry + '?')
        count += inputText.count(entry + ';')
        if entry == 'Person':
            count += inputText.count(entry + 'en ')
            count += inputText.count(entry + 'en.')
            count += inputText.count(entry + 'en,')
            count += inputText.count(entry + 'en!')
            count += inputText.count(entry + 'en?')
            count += inputText.count(entry + 'en;')
        features.append(count)
    return features;

# Given perceptron algorithm
def perceptron(x_in,y_in,rho_init,w_init, alpha) :
    rho = rho_init
    w = w_init
    x = x_in
    y = y_in
    errors = True
    loopRound = 0
    while errors :
        print 'new iteration with w: {} rho: {}'.format(w,rho)
        errors = False
        for i in range(len(y)) :
            print 'w = {}, rho = {}'.format(w, rho)
#            print 'x = {}, y = {}'.format(x[i], y[i])
            if (y[i]*(dot(w,x[i]) + rho)) <= 0 :
                w = map(sum, zip(w,map(lambda z: alpha*y[i]*z, x[i])))
                rho = rho + alpha*y[i]
                errors = True

# Initializing the stuff
rawData = readFile('/home/void/corpus.txt')
categories = categories()
omega_init = [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0]
rho_init = 0.0
alpha = 0.5
y = [1,-1,-1,1,1,1,1,-1,1,-1]
x = []

# Count the words in the rawData
nr = 0
for i in range(len(rawData)) :
    if(rawData[i] != '') :
        nr = nr + 1
        x.append(scrape(rawData[i], categories))

# Energize!
perceptron(x,y,rho_init,omega_init, alpha)
