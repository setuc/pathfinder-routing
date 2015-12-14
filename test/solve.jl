using Base.Test
using PathfinderRouting

vehicles = [ 1, 3, 5 ]
commodities = Dict('7' => 2, '4' => 6)
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

route = PathfinderRouting.optimize(vehicles, commodities, transpose(hcat(distances...)), capacities)

@test route == expected
