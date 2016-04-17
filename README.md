# The k-Vehicle Routing Optimization Problem

At the core of Pathfinder is the k-Vehicle Routing Routing (kVRP). kVRP is known to be NP-complete, which is not the type of problem fast-moving developers with a product to ship should be worrying about. This is the core of why we created Pathfinder.


## The problem

Traditionally, kVRP concerns partitioning and ordering a set of locations between a set of vehicles. Common examples include mail delivery or scheduling repairmen.

Pathfinder solves a more constrained variant of kVRP. In Pathfinder, solutions are constrained by:
* Vehicle capacities. Pathfinder allows for arbitrary capacity vectors that transports are initialized with and are updated when servicing commodities.
* Pickup-dropoff relationships. It would not make sense to drop off a commodity before picking it up or for a vehicle to drop off a commodity that a different vehicle picked up.
* Initial conditions. Pathfinder optimizes routes in real-time, which includes the scenario when commodities are already en-route.
* Arbitrary objective functions. Some kVRP approximation algorithms assume that the goal is to minimize distance and take advantage of properties of Euclidean space.


## The solution

On every computation, Pathfinder uses three optimization approaches and selects the best route yet discovered after a fixed period of time. The code for each of the three approaches, as well as the "master router" that selects the optimal route is all contained in this repository.

Each approach works well for some scenarios and poorly for others. The three approaches Pathfinder employs are:
* Linear programming using Julia, JuMP and CLP. Code is in `/linearprogramming`
* Heuristic search using Optaplanner. Code is in `/heuristicsearch`.
* Simmulated annealing using a built-from-scratch implementation. Code is in `/simulatedannealing` and at `https://github.com/csse497/simulatedannealing`.
