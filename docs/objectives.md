<style>.task-list-item { list-style: none; }</style>

Last updated on January 27, 2016 by Adam Michael

This document defines The Pathfinder's routing model and how it can be used to configure capacities, parameters and custom objective functions to specify the routing behaviour for your application.

# Contents

1. [LP Model](#Model)
2. [Objective Function DSL](#DSL)

# LP Model

## Cluster
A cluster is a set of vertexes. Each transport starting location, commodity pickup location and commodity dropoff location is a vertex. If a commodity is currently in transit by a transport, then it will have a drop off vertex, but not a pickup vertex.

Each vertex in this set is referred to as a route action. The cluster's route actions are divided into subsets as follows.

    TA = transport start actions
    PA = commodity pickup actions
    DA = commodity dropoff action
    CA = commodity actions = PA + DA
    RA = route actions = TA + CA = the cluster

Each vertex is augmented by a capacity vector.

In this document, a transport start action is used synonymously with "a transport" and a commodity (pickup or dropoff) action is used synonymously with "a commodity".

## Route
A route is a partition of the set of route actions for a cluster into disjoint directed paths. This partition follows the following rules.

    1. Every path must start with a transport action
    2. Every transport action must start its own path
    3. Every commodity pickup action must be in the same path and sequentially before its corresponding dropoff action
    4. Each component of the sum of the capacity vectors up to each vertex along a path must be nonnegative

## Variables

These binary variable sufficiently describe a route as described in the previous section

    x[k1,k2,i], k1 ∈ RA, k2 ∈ RA, i ∈ TA = transport i's path contains an edge from k1 to k2

## Parameters

    distances[k1,k2], k1 ∈ RA, k2 ∈ RA = driving distance in meters from k1 to k2
    durations[k1,k2], k1 ∈ RA, k2 ∈ RA = driving duration in seconds from k1 to k2
    request_time[d], d ∈ DA = the time that commodity with dropoff d originally requested transportation

## Supplementary Variables

These variables are not strictly necessary to compute a route, but they are derived from the variables and parameters previously listed and they help in forming complex objective functions

    y[k1,k2,i], k1 ∈ RA, k2 ∈ RA, i ∈ TA = transport i's path contains a subpath from k1 to k2
    z[k1,k2,k3], k1 ∈ RA, k2 ∈ RA, k3 ∈ TA = the subpath from a transport to commodity action k3 contains an edge from k1 to k2
    distance[i], i ∈ TA = the distance of transport i's path
    duration[i], i ∈ TA = the duration of transport i's path
    distance[d], d ∈ DA = The distance traveled by the commodity dropped off at d
    duration[d], d ∈ DA = The in transit by the commodity dropped off at d
    pickup_time[d], d ∈ DA = The time that the commodity dropped off at d will be picked up or 0 if it has already been picked up
    dropoff_time[d], d ∈ DA = The time that commodity d dropped off at d will be dropped off

<br>

# Objective Function DSL

Objective functions are the criteria used to optimize routes. Under the hood, Pathfinder uses complex mathematics to determine the optimal route at any given time. However, to use Pathfinder it is not required to understand the underlying model. Instead, objective functions are specified using a Domain Specific Language (DSL). If this seems too complicated, don't fret! The Pathfinder dashboard contains two predefined objective functions that cover common use cases: minimizing distance and minimizing time.

Note that the "objective functions" described by this document are strictly speaking not objective functions. If you're interested in how Pathfinder transforms this DSL into mathematical expressions, you can read more about the DSL Compiler.

## Concepts and Terminology

An objective function is a function from some variables to a real number and an instruction to either minimize or maximize that number. For Pathfinder, routes are calculated with cluster-granularity. The input to the objective function is the "route" for the cluster for the snapshot in time when it is calculated. There are three parts to a Pathfinder objective function: sense, context and quantity.

### Sense
This is the instruction to either minimize or maximize the function.

### Context
Since the input to the objective function is all of the routes for transports in the cluster, every objective function will involve iterating over the transports, the commodities or some Cartesian product of those sets. An expression is computed during the iteration and then either a sum, min or max is computed. This is essentially a "foreach loop" from most familiar programming languages.

For instance, I may want to minimize the maximum distance traveled by any transports. In this case, Pathfinder iterates over the set of transports and evaluates a maximum. "max" is referred to as the "method" and transports is referred to as the "entities".

Alternatively, I may want to minimize the total difference between commodity wait times. In this case, Pathfinder iterates over the set of all pairs of commodities and evaluates a sum. The method is "sum" and the entities are the Cartesian product of commodities with commodities.

### Quantity
This is the value that will be computed for each iteration of the context. It is an algebraic combination of route variables, a few Pathfinder specific keyword quantities and parameters that are configured for your application and passed in via the metadata of transports and commodities.

The currently supported operations are absolute value, add, subtract, multiply and divide. This list is admittedly limited, however that is due to the need for the expression to be representable by a "linear combination" for Pathfinder's optimizer to function correctly.


## Syntax

The Pathfinder DSL is a (very small) subset of YAML. Every objective function has the form

    sense: <Sense>
    context:
        method: <Method>
        for: <Entity Dictionary>
    quantity:
        <Expression>
        

### Sense

    Sense = min | max

### Context
    Method = sum | min | max
    Entity Dictionary =
        <name1>: commodity | transport
        <name2>: commodity | transport
        <name3>: commodity | transport
        

### Quantity
    Expression = Value | Evaluation
    
    Evaluation =
        <Function>:
            - Quantity Expression
            ...
    Value = Constant | <entity name>.<Property>
    Constant = <Number> | <Global Keyword>
    Property = <Entity Keyword> | <Parameter> 
    Function = absolute_value | add | subtract | multiply | divide

#### Global

- `now` - UTC timestamp when the route is calculated

#### Transport Keywords
- `distance` - The route length for the transport
- `duration` - The route duration for the transport

#### Commodity Keywords
- `request_time` - The UTC timestamp for when the commodity was first set to `Waiting` status
- `pickup_time` - The UTC timestamp for when the commodity will be picked up in the route
- `dropoff_time` - The UTC timestamp for when the commodity will be dropped off in the route
- `distance` - The distance that the commodity will travel according to the route

#### Parameters

These must be configured via the Pathfinder dashboard and included in the `metadata` for commodity or transports when they are created and updated via the SDKs. If the parameters are not present in the metadata, Pathfinder will make intelligent guesses (MAX\_INT, MIN\_INT, 0) based on your objective function. However, this is strongly discouraged.


## Examples
##### Minimize total transport distance
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
