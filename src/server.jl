using HttpServer

include("solve.jl")

function startserver()
  http = HttpHandler() do req::Request, res::Response
    info("Received route request: ", req.resource)
    if ismatch(r"^/route/", req.resource)
      response = solve(req.resource)
      info("Returning response: ", response)
      return Response(response)
    else
      warn("Returning 404")
      return Response(404)
    end
  end

  server = Server(http)
  run(server, 2929)
end
