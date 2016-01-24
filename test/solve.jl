using Base.Test
using PathfinderRouting

ShortestDistance = "@setObjective(model, Min, sum{distances[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=TA})"

# Single optimal solution.

transports = [ 1, 3, 5 ]
commodities = Dict(2 => 7, 6 => 4)
distances = []
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,0,9,1,9,9,9])
push!(distances, [0,9,0,9,0,9,1])
push!(distances, [9,9,9,0,9,1,9])
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,9,9,9,9,0,9])
push!(distances, [9,1,9,9,9,9,0])
capacities = [Dict{Int64,Any}(1 => 1, 2 => -1, 3 => 1, 4 => 1, 5 => 1, 6 => -1, 7 => 1)]
parameters = Dict()

expected = []
push!(expected, [1])
push!(expected, [3,7,2,4,6])
push!(expected, [5])

route = PathfinderRouting.optimize(transports, commodities, transpose(hcat(distances...)), transpose(hcat(distances...)), capacities, parameters, ShortestDistance)

@test route == expected

# Optimal solution is constrained by capacity.

transports = [ 1, 3, 5 ]
commodities = Dict(2 => 7, 6 => 4)
distances = []
push!(distances, [0,9,0,9,0,9,9])
push!(distances, [9,0,9,8,9,9,9])
push!(distances, [0,9,0,9,0,9,1])
push!(distances, [9,8,9,0,9,1,9])
push!(distances, [0,1,0,9,0,9,9])
push!(distances, [9,9,9,9,9,0,9])
push!(distances, [9,9,9,1,9,9,0])
capacities = [Dict{Int64,Any}(1 => 1, 2 => -1, 3 => 1, 4 => 1, 5 => 1, 6 => -1, 7 => 1)]
parameters = Dict()

expected = []
push!(expected, [1])
push!(expected, [3,7,2,4,6])
push!(expected, [5])

route = PathfinderRouting.optimize(transports, commodities, transpose(hcat(distances...)), transpose(hcat(distances...)), capacities, parameters, ShortestDistance)

@test route == expected


# Maximize distance traveled by vehicle 5.

transports = [ 1, 3, 5 ]
commodities = Dict(2 => 7, 6 => 4)
distances = []
push!(distances, [1,1,1,1,1,1,1])
push!(distances, [1,1,1,9,1,1,1])
push!(distances, [1,1,1,1,1,1,9])
push!(distances, [1,1,1,1,1,9,1])
push!(distances, [1,1,1,1,1,1,9])
push!(distances, [1,1,1,1,1,1,5])
push!(distances, [1,9,1,1,1,1,1])
capacities = [Dict{Int64,Any}(1 => 1, 2 => -1, 3 => 1, 4 => 1, 5 => 1, 6 => -1, 7 => 1)]
parameters = Dict()
objective = "@setObjective(model, Min, -1*sum{x[k1,k2,5]*distances[k1,k2], k1=RA, k2=RA})"

expected = []
push!(expected, [1])
push!(expected, [3])
push!(expected, [5,7,2,4,6])

route = PathfinderRouting.optimize(transports, commodities, transpose(hcat(distances...)), transpose(hcat(distances...)), capacities, parameters, objective)

@test route == expected

# Vehicle 5 starts with a commodity in route.

transports = [ 1, 3, 5 ]
commodities = Dict(2 => 5, 6 => 4)
distances = []
push!(distances, [0,9,0,9,0,9])
push!(distances, [9,0,9,9,9,9])
push!(distances, [0,9,0,1,0,9])
push!(distances, [9,9,9,0,9,1])
push!(distances, [0,99,0,9,0,9])
push!(distances, [9,9,9,9,9,0])
capacities = [Dict{Int64,Any}(1 => 1, 2 => -1, 3 => 1, 4 => 1, 5 => 0, 6 => -1)]
parameters = Dict()

expected = []
push!(expected, [1])
push!(expected, [3,4,6])
push!(expected, [5,2])

route = PathfinderRouting.optimize(transports, commodities, transpose(hcat(distances...)), transpose(hcat(distances...)), capacities, parameters, ShortestDistance)

@test route == expected

# Complex DSL output.

transports = [ 1, 2 ]
commodities = Dict(4 => 3, 6 => 5)
distances = []
push!(distances, [0,1,1,1,1,1])
push!(distances, [1,0,1,1,1,1])
push!(distances, [1,1,0,1,1,1])
push!(distances, [1,1,1,0,9,1])
push!(distances, [1,1,1,9,0,1])
push!(distances, [1,1,1,1,1,0])
capacities = []
push!(capacities, Dict(1 => 2, 2 => 1, 3 => 1, 4 => -1, 5 => 2, 6 => -1))
parameters = Dict("request_time" => Dict(1 => 0, 2 => 0, 3 => -10, 4 => -10, 5 => 0, 6 => 0))

objective = """
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

expected = []
push!(expected, [1, 3, 4, 5, 6])
push!(expected, [2])

route = PathfinderRouting.optimize(transports, commodities, transpose(hcat(distances...)), transpose(hcat(distances...)), capacities, parameters, objective, 0)

@test route == expected
