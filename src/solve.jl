using Logging

f = open(dirname(@__FILE__) * "/base.jl")
original = readall(f)
close(f)

function optimize(vehicles, commodities, distances, capacities, objective)
  code = replace(original, "FUCKTHIS", objective)
  return include_string(code)(vehicles, commodities, distances, capacities)
end
