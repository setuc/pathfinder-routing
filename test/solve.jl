using Base.Test
using PathfinderRouting

ShortestDistance = "sum{distances[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=VA}"

# Single optimal solution.

vehicles = [ 1, 3, 5 ]
commodities = Dict(7 => 2, 4 => 6)
distances = []
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,0,9,1,9,9,9])
push!(distances, [0,9,0,9,0,9,1])
push!(distances, [9,9,9,0,9,1,9])
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,9,9,9,9,0,9])
push!(distances, [9,1,9,9,9,9,0])
capacities = []
push!(capacities, Dict(1 => 1, 3 => 1, 4 => 1, 5 => 1, 7 => 1))

expected = []
push!(expected, [1])
push!(expected, [3,7,2,4,6])
push!(expected, [5])

route = PathfinderRouting.optimize(vehicles, commodities, transpose(hcat(distances...)), capacities, ShortestDistance)

@test route == expected

# Optimal solution is constrained by capacity.

vehicles = [ 1, 3, 5 ]
commodities = Dict(7 => 2, 4 => 6)
distances = []
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,0,9,9,9,9,9])
push!(distances, [0,9,0,9,0,9,1])
push!(distances, [9,9,9,0,9,1,9])
push!(distances, [0,1,0,9,0,9,9])
push!(distances, [9,9,9,9,9,0,9])
push!(distances, [9,9,9,1,9,9,0])
capacities = []
push!(capacities, Dict(1 => 1, 3 => 1, 4 => 1, 5 => 1, 7 => 1))

expected = []
push!(expected, [1])
push!(expected, [3,7,2,4,6])
push!(expected, [5])

route = PathfinderRouting.optimize(vehicles, commodities, transpose(hcat(distances...)), capacities, ShortestDistance)

@test route == expected


# Maximize distance traveled by vehicle 5.

CustomObjective = "-1*sum{distances[k1,k2]*x[k1,k2,5],k1=RA,k2=RA}"

vehicles = [ 1, 3, 5 ]
commodities = Dict(7 => 2, 4 => 6)
distances = []
push!(distances, [1,1,1,1,1,1,1])
push!(distances, [1,1,1,9,1,1,1])
push!(distances, [1,1,1,1,1,1,9])
push!(distances, [1,1,1,1,1,9,1])
push!(distances, [1,1,1,1,1,1,9])
push!(distances, [1,1,1,1,1,1,5])
push!(distances, [1,9,1,1,1,1,1])
capacities = []
push!(capacities, Dict(1 => 1, 3 => 1, 4 => 1, 5 => 1, 7 => 1))
objective = :(-1*sum{x[k1,k2,5]*distances[k1,k2], k1=RA, k2=RA})

expected = []
push!(expected, [1])
push!(expected, [3])
push!(expected, [5,7,2,4,6])

route = PathfinderRouting.optimize(vehicles, commodities, transpose(hcat(distances...)), capacities, objective)

@test route == expected
