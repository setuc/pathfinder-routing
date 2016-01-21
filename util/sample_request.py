#!/usr/bin/env python

import requests

def request_route(route_url):
  data = {
    'vehicles': [ 1, 3, 5 ],
    'commodities': {
      2: 5,
      6: 4
    },
    'durations': [
      [ 0, 1, 1, 1, 1, 1 ],
      [ 1, 0, 1, 1, 1, 1 ],
      [ 1, 1, 0, 1, 1, 1 ],
      [ 1, 1, 1, 0, 1, 1 ],
      [ 1, 1, 1, 1, 0, 1 ],
      [ 1, 1, 1, 1, 1, 0 ],
    ],
    'distances': [
      [ 0, 9, 0, 9, 0, 9 ],
      [ 9, 0, 9, 9, 9, 9 ],
      [ 0, 9, 0, 9, 0, 9 ],
      [ 9, 9, 9, 0, 9, 1 ],
      [ 0, 99, 0, 9, 0, 9 ],
      [ 9, 9, 9, 9, 9, 0 ],
    ],
    'capacities': [
      { 1: 1,
        2: -1,
        3: 1,
        4: 1,
        5: 0,
        6: -1
      }
    ],
    'commodityParameters': [
      { 4: 4,
        7: 0
      }
    ],
    'vehicleParameters': [
      { 1: 7,
        3: 7,
        5: 7
      }
    ],
    'objective': 'sum{distances[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=VA}'
  }
  r = requests.post(route_url, json=data)
  print 'Status code: ' + str(r.status_code)
  print 'Response json: ' + str(r.json())
  

if __name__ == '__main__':
  request_route('http://localhost:2929')
