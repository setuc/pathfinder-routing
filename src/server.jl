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
    distances = transpose(hcat(jsonreq["distances"]...))
    result = optimize(jsonreq["vehicles"], jsonreq["commodities"], distances, jsonreq["capacities"])
    response = JSON.json(Dict("routes" => result))
    info("Returning response: ", response)
    return Response(response)
  end

  server = Server(http)
  run(server, 2929)
end
