# Pathfinder Route Optimization Web Service
[![Coverage Status](https://coveralls.io/repos/CSSE497/PathfinderRouting/badge.svg?branch=dev&service=github)](https://coveralls.io/github/CSSE497/PathfinderRouting?branch=dev)
[![Stories in Ready](https://badge.waffle.io/CSSE497/pathfinder-routing.svg?label=ready&title=Ready)](http://waffle.io/CSSE497/pathfinder-routing)
[![Stories in Progress](https://badge.waffle.io/CSSE497/pathfinder-routing.svg?label=In%20Progress&title=In%20Progress)](http://waffle.io/CSSE497/pathfinder-routing)
[![Stories under Review](https://badge.waffle.io/CSSE497/pathfinder-routing.svg?label=Under%20Review&title=Under%20Review)](http://waffle.io/CSSE497/pathfinder-routing)

While the bulk of the Pathfinder business logic is contained in the [Scala Play! backend server](https://github.com/csse497/pathfinder-server), the problem of route optimization is implemented as a stand-alone Julia microservice.

## Model
Pathfinder models route optimization as a linear programming problem. The function to be optimized and the constraints are specific to each cluster at a snapshot in time for an application.

### Parameters
This section is under construction.

### Variables
This section is under construction.

### Objective function
This section is under construction.

### Constraints
This section is under construction.

## Solution implementation
The above problem is solved using the Julia extension of the GNU Linear Programming Kit (GLPK).

### Request format
This section is under construction.

### Response format
This section is under construction.
