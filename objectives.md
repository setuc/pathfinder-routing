<style>.task-list-item { list-style: none; }</style>

# Route Computation

Last updated on January 23, 2016 by Adam Michael

This document defines The Pathfinder's routing model and how it can be used to configure capacities, parameters and custom objective functions to specify the routing behaviour for your application.

## Model

### Cluster
A cluster is a set of vertexes. Each transport starting location, commodity pickup location and commodity dropoff location is a vertex. If a commodity is currently in transit by a transport, then it will have a drop off vertex, but not a pickup vertex.

Each vertex in this set is referred to as a route action. The cluster's route actions are divided into subsets as follows.

    TA = transport start actions
    PA = commodity pickup actions
    DA = commodity dropoff action
    CA = commodity actions = PA + DA
    RA = route actions = TA + CA = the cluster

Each vertex is augmented by a capacity vector.

In this document, a transport start action is used synonymously with "a transport" and a commodity (pickup or dropoff) action is used synonymously with "a commodity".

### Route
A route is a partition of the set of route actions for a cluster into disjoint directed paths. This partition follows the following rules.

    1. Every path must start with a transport action
    2. Every transport action must start its own path
    3. Every commodity pickup action must be in the same path and sequentially before its corresponding dropoff action
    4. Each component of the sum of the capacity vectors up to each vertex along a path must be nonnegative

### Variables

    x[k1,k2,i] = transport i's path contains an edge from k1 to k2
    y[k1,k2,i] = transport i's path contains a subpath from k1 to k2
    z[k1,k2,k3] = the subpath from a transport to commodity action k3 contains an edge from k1 to k2


### Parameters

    distance[k1,k2] = driving distance in meters from k1 to k2
    duration[k1,k2] = driving duration in seconds from k1 to k2


### Examples of Objective Functions
#### Minimize total transport distance
- [x] Julia

        @setObjective(model, Min, sum{distance[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=TA})

- [x] DSL

        sense: min
        context:
            method: sum
            for:
                t: transport
        quantity: t.distance

####  Minimize total transport time
- [x] Julia

        @setObjective(model, Min, sum{duration[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=TA})

- [x] DSL

        sense: min
        context:
            method: sum
            for:
                t: transport
        quantity: t.duration


#### Minimize max transport distance
- [x] Julia

        @defVar(model, _value, Int)
        for i in TA
            @addConstraint(model, _value >= sum{distance[k1,k2]*x[k1,k2,i], k1=RA, k2=RA})
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            method: max
            for:
                t: transport
        quantity: t.distance

#### Minimize max transport route duration
- [x] Julia

        @defVar(model, _value, Int)
        for i in TA
            @addConstraint(model, _value >= sum{duration[k1,k2]*x[k1,k2,i], k1=RA, k2=RA})
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            method: max
            for:
                t: transport
        quantity: t.duration

#### Minimize max time until passenger pickup
- [x] Julia

        @defVar(model, _value, Int)
        for i in PA
            @addConstraint(model, _value >= sum{duration[k1,k2]*z[k1,k2,i], k1=RA, k2=RA})
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            method: max
            for:
                c: commodity
        quantity: c.pickup_time

#### Minimize max fuel used by a transport
- [x] Julia
        
        @defVar(model, _value, Int)
        for i in PA
            @addConstraint(model, _value >= sum{distance[k1,k2]*1/mpg[i]*x[k1,k2,i], k1=RA, k2=RA})
        end
        @setObjective(model, Min, _value)
        
- [x] DSL

        sense: min
        context:
            method: sum
            for:
                t: transport
        quantity:
            divide:
                - t.distance
                - t.mpg            

#### Minimize total fuel used by transports
- [x] Julia
        
        mpg = parameters['mpg']
        @setObjective(model, Min, sum{distance[k1,k2]*1/mpg[i]*x[k1,k2,i],k1=RA,k2=RA,i=TA})
        
- [x] DSL

        sense: min
        context:
            method: sum
            for:
                t: transport
        quantity:
            divide:
                - t.distance
                - t.mpg

#### Minimize max time until passenger dropoff
- [x] Julia

        @defVar(model, _value, Int)
        for i in DA
            @addConstraint(model, _value >= sum{duration[k1,k2]*z[k1,k2,i], k1=RA, k2=RA})
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            - method: max
            - for:
                c: commodity
        quantity:
            subtract:
                - c.dropoff_time
                - now
        

#### Minimize maximum time until dropoff including past wait time
- [x] Julia

        requesttime = parameters['requesttime']
        @defVar(model, _value, Int)
        for i in DA
            @addConstraint(model, _value >= time - requesttime[i] + sum{duration[k1,k2]*z[k1,k2,i], k1=RA, k2=RA}
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            - method: max
            - for:
                c: commodity
        quantity:
            subtract:
                - c.dropoff_time
                - c.request_time

#### Minimize time until pickup weighted by customer priority
- [x] Julia

        priority = parameters['priority']
        @setObjective(model, Min, sum{z[k1,k2,k3]*duration[k1,k2]*priority[k3],k1=RA,k2=RA,k3=PA})
        
- [x] DSL

        sense: min
        context:
            method: sum
            for:
                c: commodity
        quantity:
            multiply:
                - subtract:
                    - c.pickup_time
                    - now
                - c.priority                

#### Minimize total time from now until drop off weighted by customer priority
- [x] Julia

        priority = parameters['priority']
        @setObjective(model, Min, sum{z[k1,k2,k3]*duration[k1,k2]*priority[k3],k1=RA,k2=RA,k3=DA})
        
- [x] DSL

        sense: min
        context:
            method: sum
            for:
                c: commodity
        quantity:
            multiply:
                - substract:
                    - c.dropoff_time
                    - now
                - c.priority

#### Minimize maximum distance traveled by commodity

- [ ] Julia

        @defVar(model, _value, Int)
        for i in DA
            ???
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            method: max
            for:
                c: commodity
        quantity: c.distance


#### Minimize maximum difference between passenger total wait times

- [ ] Julia

        @defVar(model, _value, Int)
        for c1 in CA
            for c2 in CA
                ???
            end
        end
        @setObjective(model, Min, _value)

- [x] DSL

        sense: min
        context:
            method: max
            for:
                c1: commodity
                c2: commodity
        quantity:
            absolute_value:
                subtract:
                    - subtract:
                        - c1.dropoff_time
                        - c1.request_time
                    - subtract:
                        - c2.dropoff_time
                        - c2.dropoff_time

#### Minimize sum of differences between passenger total wait times

- [x] Julia - This one I have tested extensively. It is a good example of how quantity is expanded.
        
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

- [x] DSL

        sense: min
        context:
            method: sum
            for:
                c1: commodity
                c2: commodity
        quantity:
            absolute_value:
                - subtract:
                    - subtract:
                        - c1.dropoff_time
                        - c1.request_time
                    - subtract:
                        - c2.dropoff_time
                        - c2.request_time

# DSL Concepts

## Sense
Min or max

## Context
- `method` - sum or max or min
- `for` - a list of entities to iterate over

## Quantity
- `add`, `subtract`, `multiply`, `divide`

## DSL Keywords

- `now` - Global timestamp
- `distance` - This is the route length for transports
- `duration` - For transport
- `request_time` - For commodity
- `pickup_time` - For commodity
- `dropoff_time` - For commodity
- `distance` - This is subpath length for commodities
