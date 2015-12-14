using HttpServer
using JSON
using Logging

Logging.configure(level=DEBUG)


include("solve.jl")

function startserver()
  http = HttpHandler() do req::Request, res::Response
    info("Received route request: ", JSON.parse(bytestring(req.data)))
    jsonreq = JSON.parse(bytestring(req.data))
    vehicles = jsonreq["vehicles"]
    commodities = jsonreq["commodities"]
    commodities = [parse(Int64, p) => commodities[p] for p=keys(commodities)]
    capacities = jsonreq["capacities"]
    capacities = [[parse(Int64, a) => c[a] for a=keys(c)] for c=capacities]
    distances = transpose(hcat(jsonreq["distances"]...))
    result = optimize(vehicles, commodities, distances, capacities)
    response = JSON.json(Dict("routes" => result))
    info("Returning response: ", response)
    return Response(response)
  end

  server = Server(http)
  run(server, 2929)
end
