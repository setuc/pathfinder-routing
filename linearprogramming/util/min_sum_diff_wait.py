#!/usr/bin/env python

import requests

def request_route(route_url):
  data = {
    'vehicles': [ 1, 2 ],
    'commodities': {
      4: 3,
      6: 5
    },
    'durations': [
      [ 0, 1, 1, 1, 1, 1 ],
      [ 1, 0, 1, 1, 1, 1 ],
      [ 1, 1, 0, 1, 1, 1 ],
      [ 1, 1, 1, 0, 9, 1 ],
      [ 1, 1, 1, 9, 0, 1 ],
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
    'capacities': {
      'capacity': {
        1: 2,
        2: 1,
        3: 1,
        4: -1,
        5: 2,
        6: -1
      }
    },
    'parameters': {
      'request_time': {
        1: 0,
        2: 0,
        3: -10,
        4: -10,
        5: 0,
        6: 0
      }
    },
    'objective': """
        @defVar(model, _value, Int)
        @defVar(model, _tmp1[DA,DA], Int)
        @defVar(model, _tmp2[DA,DA], Int)
        @defVar(model, _tmp3[DA,DA], Int)
        @defVar(model, _tmp4[DA,DA], Int)
        @defVar(model, pos_tmp4[DA,DA] >= 0, Int)
        @defVar(model, neg_tmp4[DA,DA] >= 0, Int)
        for c1 in DA
        for c2 in DA
        @addConstraint(model, _tmp1[c1,c2] == dropoff_time[c1] - parameters["request_time"][c1])
        @addConstraint(model, _tmp2[c1,c2] == dropoff_time[c2] - parameters["request_time"][c2])
        @addConstraint(model, _tmp3[c1,c2] == _tmp1[c1,c2] - _tmp2[c1,c2])
        @addConstraint(model, _tmp4[c1,c2] == pos_tmp4[c1,c2] + neg_tmp4[c1,c2])
        @addConstraint(model, _tmp3[c1,c2] == pos_tmp4[c1,c2] - neg_tmp4[c1,c2])
        end
        end
        @addConstraint(model, _value == sum{_tmp4[c1,c2],c1=DA,c2=DA})
        @setObjective(model, Min, _value)
    """
  }
  r = requests.post(route_url, json=data)
  print 'Status code: ' + str(r.status_code)
  print 'Response json: ' + str(r.json())
  

if __name__ == '__main__':
  request_route('http://localhost:2929')
