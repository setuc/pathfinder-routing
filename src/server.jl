using HttpServer
using JSON
using Logging

Logging.configure(level=DEBUG)


include("solve.jl")

function parsevehicles(raw)
  return raw["vehicles"]
end

function parsecommodities(raw)
  commodities = raw["commodities"]
  return [parse(Int64, p) => commodities[p] for p=keys(commodities)]
end

function parsecapacities(raw)
  capacities = raw["capacities"]
  return [[parse(Int64, a) => capacities[c][a] for a=keys(capacities[c])] for c=keys(capacities)]
end

function parsedistances(raw)
  return transpose(hcat(raw["distances"]...))
end

function parsedurations(raw)
  return transpose(hcat(raw["durations"]...))
end

function parseobjective(raw)
  if "objective" in keys(raw)
    return raw["objective"]
  else
    return "sum{distances[k1,k2]*x[k1,k2,i],k1=RA,k2=RA,i=VA}"
  end
end

function startserver()
  http = HttpHandler() do req::Request, res::Response
    info("Received route request: ", JSON.parse(bytestring(req.data)))
    jsonreq = JSON.parse(bytestring(req.data))
    vehicles = parsevehicles(jsonreq)
    commodities = parsecommodities(jsonreq)
    capacities = parsecapacities(jsonreq)
    distances = parsedistances(jsonreq)
    durations = parsedurations(jsonreq)
    objective = parseobjective(jsonreq)
    result = optimize(vehicles, commodities, distances, durations, capacities, objective)
    response = JSON.json(Dict("routes" => result))
    info("Returning response: ", response)
    return Response(response)
  end

  server = Server(http)
  run(server, 2929)
end
