#!/usr/bin/env python

import json
import requests
import time
import random
import sys
import math

def distance(a, b):
  x1, y1 = a
  x2, y2 = b
  return int(24494*math.sqrt(math.pow(x1-x2,2) + math.pow(y1-y2,2)))

M = 30
MAX_T = 10
MAX_C = 8
coordinates = [(random.random(), random.random()) for i in range(M)]
distance_matrix = [[distance(coordinates[a],coordinates[b]) for a in range(M)] for b in range(M)]

def data(t, c):
  if t + 2*c > len(distance_matrix):
    raise Exception
  transports = range(1, t + 1)
  commodities = { x: x + c for x in range(t + 1, t + c + 1) }
  return {
    'transports': transports,
    'commodities': commodities,
    'durations': distance_matrix,
    'distances': distance_matrix,
    'capacities': {
      'capacity': dict({t: 3 for t in transports}, **dict({commodities[d]: 1 for d in commodities}, **{d: -1 for d in commodities}))
    },
    'parameters': {
    },
    'objective': """
    @setObjective(model, Min, sum{distance[i],i=TA})
    """
  }

def time_request(url, data):
  start = time.time()
  r = requests.post(url, json=data)
  print "{0:.2f}".format(time.time() - start),
  print "(" + str(route_length(json.loads(r.text)['routes'])) +") |",
  sys.stdout.flush()

def test(url, t, c):
  time_request(url, data(t, c))
  #time_request('http://routing3.thepathfinder.xyz:8080/pathfinder-routing', data(t, c))

def testlocal(t, c):
  time_request('http://localhost:8080/routing', data(t, c))

def testold(t, c):
  time_request('http://routing.thepathfinder.xyz', data(t, c))

def route_length(routes):
  return sum(distance_matrix[a-1][b-1] for [a, b] in sum([zip(r[:len(r)-1],r[1:]) for r in routes], []))

def pretty_test(name, url):
  print name
  print
  print '||',
  for i in range(1,MAX_C):
    print 'C='+str(i)+' |',
  print
  for i in range(0,MAX_C):
    print '|---',
  print '|'
  for i in range(1,MAX_T):
    print '|T='+str(i)+'|',
    [test(url,i,c) for c in range(1,MAX_C)]
    print

if __name__ == '__main__':
  pretty_test('SA - Linear (T=100, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-linear-100')
  pretty_test('SA - Linear (T=1000, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-linear-1000')
  pretty_test('SA - Linear (T=10000, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-linear-10000')
  pretty_test('SA - Exponential (T=100, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-exp-100')
  pretty_test('SA - Exponential (T=1000, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-exp-1000')
  pretty_test('SA - Exponential (T=10000, N=1000000)', 'http://routing3.thepathfinder.xyz:8080/pathfinder-routing-exp-10000')
  pretty_test('Optaplanner', 'http://routing2.thepathfinder.xyz')
