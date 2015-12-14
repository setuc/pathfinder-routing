using Base.Test
using PathfinderRouting

vehicles = [ 1, 3, 5 ]
commodities = Dict('7' => 2, '4' => 6)
distances = []
push!(distances, [0,1,1,1,9,1,1])
push!(distances, [9,0,1,1,9,1,1])
push!(distances, [9,1,0,1,9,1,1])
push!(distances, [9,1,1,0,9,1,1])
push!(distances, [9,1,1,1,0,1,1])
push!(distances, [9,1,1,1,9,0,1])
push!(distances, [9,1,1,1,9,1,0])
capacities = []
push!(capacities, Dict(1 => 1, 3 => 1, 4 => 1, 5 => 1, 7 => 1))

expected = []
push!(expected, [1])
push!(expected, [3,7,2,4,6])
push!(expected, [5])

route = PathfinderRouting.optimize(vehicles, commodities, hcat(distances...), capacities)

@test route == expected
