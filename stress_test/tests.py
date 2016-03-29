#!/usr/bin/env python

import json
import requests
import time
import random

M = 30
distance_matrix = [[0 if r==c else random.choice(range(1, 100)) for c in range(0,30)] for r in range (0, 30)]

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
      'capacity': dict({t: 1 for t in transports}, **dict({commodities[d]: 1 for d in commodities}, **{d: -1 for d in commodities}))
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
  print "(" + str(route_length(json.loads(r.text)['routes'])) +")|",
  #print r.status_code
  #print 'Length: ' + str(route_length(json.loads(r.text)['routes']))
  #return time.time() - start

def test(t, c):
  #print "Time for {} transports and {} commodities".format(t, c)
  time_request('http://routing2.thepathfinder.xyz', data(t, c))
  #print 'Time: ' + str(time_request('http://routing2.thepathfinder.xyz', data(t, c)))

def testlocal(t, c):
  time_request('http://localhost:8080/routing', data(t, c))

def testold(t, c):
  time_request('http://routing.thepathfinder.xyz', data(t, c))

def route_length(routes):
  #print 'Routes: ' + str(routes)
  return sum(distance_matrix[a-1][b-1] for [a, b] in sum([zip(r[:len(r)-1],r[1:]) for r in routes], []))

if __name__ == '__main__':
  print '|T=1|',
  [test(1,c) for c in range(1, 5)]
  print
  print '|T=2|',
  [test(2,c) for c in range(1, 4)]
  print
  print '|T=3|',
  [test(3,c) for c in range(1, 4)]
  print
  print '|T=4|',
  [test(4,c) for c in range(1, 4)]
  print
  print '|T=5|',
  [test(5,c) for c in range(1, 4)]
  print
  print '|T=6|',
  [test(6,c) for c in range(1, 3)]
  print
  print '|T=7|',
  [test(7,c) for c in range(1, 3)]
  print
  print '|T=8|',
  [test(8,c) for c in range(1, 3)]
  print
  print '|T=9|',
  [test(9,c) for c in range(1, 3)]
  print 'OLD'
  print '|T=1|',
  [testold(1,c) for c in range(1, 5)]
  print
  print '|T=2|',
  [testold(2,c) for c in range(1, 4)]
  print
  print '|T=3|',
  [testold(3,c) for c in range(1, 4)]
  print
  print '|T=4|',
  [testold(4,c) for c in range(1, 4)]
  print
  print '|T=5|',
  [testold(5,c) for c in range(1, 4)]
  print
  print '|T=6|',
  [testold(6,c) for c in range(1, 3)]
  print
  print '|T=7|',
  [testold(7,c) for c in range(1, 3)]
  print
  print '|T=8|',
  [testold(8,c) for c in range(1, 3)]
  print
  print '|T=9|',
  [testold(9,c) for c in range(1, 3)]

