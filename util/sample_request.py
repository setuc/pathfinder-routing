#!/usr/bin/env python

import requests

def request_route(route_url):
  data = {
    'vehicles': [ 1, 3, 5 ],
    'commodities': {
      0: 2,
      4: 6
    },
    'times': [
      [ 0, 1, 1, 1, 1, 1, 1 ],
      [ 1, 0, 1, 1, 1, 1, 1 ],
      [ 1, 1, 0, 1, 1, 1, 1 ],
      [ 1, 1, 1, 0, 1, 1, 1 ],
      [ 1, 1, 1, 1, 0, 1, 1 ],
      [ 1, 1, 1, 1, 1, 0, 1 ],
      [ 1, 1, 1, 1, 1, 1, 0 ],
    ],
    'distances': [
      [ 0, 1, 1, 1, 1, 1, 1 ],
      [ 1, 0, 1, 1, 1, 1, 1 ],
      [ 1, 1, 0, 1, 1, 1, 1 ],
      [ 1, 1, 1, 0, 1, 1, 1 ],
      [ 1, 1, 1, 1, 0, 1, 1 ],
      [ 1, 1, 1, 1, 1, 0, 1 ],
      [ 1, 1, 1, 1, 1, 1, 0 ],
    ],
    'capacities': [
      { 0: 1,
        1: 1,
        3: 1,
        4: 1,
        5: 1
      }
    ],
    'commodityParameters': [
      { 0: 4,
        4: 0
      }
    ],
    'vehicleParameters': [
      { 1: 7,
        3: 7,
        5: 7
      }
    ],
    'objective': '2+2'
  }
  r = requests.post(route_url, json=data)
  print 'Status code: ' + str(r.status_code)
  print 'Response json: ' + str(r.json())
  

if __name__ == '__main__':
  request_route('http://localhost:2929/route/')
