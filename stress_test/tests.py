#!/usr/bin/env python

import requests
import time

distance_matrix = [
  [ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1 ],
  [ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 ]
]

def data(t, c):
  if t + 2*c > len(distance_matrix):
    raise Exception
  transports = range(1, t + 1)
  commodities = { x: x + c for x in range(t + 1, t + c + 1) }
  return {
    'vehicles': transports,
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

def time_request(data):
  start = time.time()
  r = requests.post('http://routing.thepathfinder.xyz', json=data)
  print r.status_code
  return time.time() - start

if __name__ == '__main__':
  for t in range(1, len(distance_matrix) - 1):
    for c in range(1, (len(distance_matrix) - t) / 2 + 1):
      print "Time for {} transports and {} commodities".format(t, c)
      print time_request(data(t, c))
