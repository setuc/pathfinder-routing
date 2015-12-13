#!/usr/bin/env python

import requests

def request_route(route_url):
  data = {'vehicles': [1, 3, 5], 'commodities': {0: 2, 4: 6}}
  r = requests.post(route_url, json=data)
  print 'Status code: ' + str(r.status_code)
  print 'Response json: ' + r.json()
  

if __name__ == '__main__':
  request_route('http://localhost:2929/route/')
