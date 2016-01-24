using Logging

original = """
using JuMP
using Logging

function routecalculation(vehicles, commodities, distances, durations, capacities, parameters)
  VA = vehicles
  DA = [p for p=keys(commodities)]
  PA = [d for d=values(commodities)]
  CA = setdiff(union(PA, DA), VA)
  RA = union(PA, DA, VA)

  info("Capacities: ", capacities)

  model = Model()
  now = time()

  @defVar(model, pickup_time[DA], Int)
  @defVar(model, dropoff_time[DA], Int)
  @defVar(model, distance[RA], Int)
  @defVar(model, duration[TA], Int)

  # x[k1,k2,i] = vehicle i's path contains an edge from k1 to k2
  @defVar(model, 0 <= x[RA,RA,VA] <= 1, Int)

  # y[k1,k2,i] = vehicle i's path contains a subpath from k1 to k2
  @defVar(model, 0 <= y[RA,RA,VA] <= 1, Int)

  # z[k1,k2,k3] = the subpath starting at a vehicle and ending at k3 contains
  # an edge from k1 to k2
  @defVar(model, 0 <= z[RA,RA,CA] <= 1, Int)

  @defVar(model, r[RA,RA,VA], Int)
  @defVar(model, rpos[RA,RA,VA] >= 0, Int)
  @defVar(model, rneg[RA,RA,VA] >= 0, Int)

  @defVar(model, q[RA,RA,VA], Int)
  @defVar(model, qpos[RA,RA,VA] >= 0, Int)
  @defVar(model, qneg[RA,RA,VA] >= 0, Int)

  for i in TA
    @addConstraint(model, duration[i] == sum{durations[k1,k2]*x[k1,k2,i],k1=RA,k2=RA})
    @addConstraint(model, distance[i] == sum{distances[k1,k2]*x[k1,k2,i],k1=RA,k2=RA})
  end

  for c in DA
    @addConstraint(model, distance[c] == sum{distances[k1,k2]*z[k1,k2,c],k1=RA,k2=RA})
  end

  for c in capacities
    for k2 in CA
      for i in VA
        # Every route segment obeys constraint limits
        @addConstraint(model, sum{c[k1]*y[k1,k2,i], k1=CA} <= c[i])
      end
    end
  end

  for k1 in RA
    for k2 in RA
      for k3 in CA
        # Definition of z
        @addConstraint(model, 2*z[k1,k2,k3] <= sum{x[k1,k2,i]+y[k1,k3,i], i=VA})
        @addConstraint(model, z[k1,k2,k3] >= sum{x[k1,k2,i]+y[k1,k3,i], i=VA} - 1)
      end
    end
  end

  for k1 in RA
    # Every node has at most one sucessor.
    @addConstraint(model, sum{x[k1,k2,i], k2=RA, i=VA} <= 1)
    for k2 in RA
      for i in VA
        # Definition of r
        @addConstraint(model, r[k1,k2,i] == sum{y[k3,k2,i] - y[k3,k1,i], k3=RA} - 1)
        @addConstraint(model, r[k1,k2,i] == rpos[k1,k2,i] - rneg[k1,k2,i])
        @addConstraint(model, 1 - x[k1,k2,i] <= rpos[k1,k2,i] + rneg[k1,k2,i])
        # Definition of q
        @addConstraint(model, q[k1,k2,i] == x[k1,k2,i] - y[k1,k2,i] - 1)
        @addConstraint(model, q[k1,k2,i] == qpos[k1,k2,i] - qneg[k1,k2,i])
        @addConstraint(model, qpos[k1,k2,i] + qneg[k1,k2,i] >= 1)

        # Y includes X
        @addConstraint(model, y[k1,k2,i] - x[k1,k2,i] >= 0)

        # Y route implies X route
        @addConstraint(model, sum{x[k1,k3,i]+x[k3,k2,i],k3=RA} >= 2*y[k1,k2,i])
      end
    end
  end

  for k in RA
    for i in VA
      # A route action cannot occur before itself.
      @addConstraint(model, x[k,k,i] == 0)
      @addConstraint(model, y[k,k,i] == 0)
    end
  end

  for d in keys(commodities)
    # Commodity pickups are in the same route as their dropoffs.
    @addConstraint(model, sum{y[commodities[d],d,i], i=VA} == 1)
    if commodities[d] in CA
      @addConstraint(model, pickup_time[d] - now == sum{z[k1,k2,commodities[d]]*durations[k1,k2],k1=RA,k2=RA})
    end
    @addConstraint(model, dropoff_time[d] - now == sum{z[k1,k2,d]*durations[k1,k2],k1=RA,k2=RA})
  end

  for k in CA
    # Every CA has a predecessor.
    @addConstraint(model, sum{x[a,k,i], a=RA, i=VA} == 1)
    for i in VA
      # Flow into and out of a vertex occur in the same route.
      @addConstraint(model, sum{x[a,k,i]-x[k,a,i], a=RA} >= 0)
    end
  end

  for a in RA
    for b in CA
      for c in CA
        for i in VA
          @addConstraint(model, y[a,b,i] >= x[a,c,i] + y[c,b,i] - 1)
        end
      end
    end
  end

  for k1 in CA
    @addConstraint(model, sum{y[k3,k1,i], i=VA, k3=RA} >= 1)
    for k2 in RA
      @addConstraint(model, sum{y[k1,k2,i] + y[k2,k1,i], i=VA} <= 1)
    end
  end


  for k1 in RA
    for k2 in VA
      for i in VA
        # A vehicle start action does not have a predecessor.
        @addConstraint(model, x[k1,k2,i] == 0)
        if k2 != i
          # A vehicle cannot be in another vehicle's route.
          @addConstraint(model, x[k2, k1, i] == 0)
        end
      end
    end
  end

  for k1 in VA
    for i in VA
      if k1 != i
        for k2 in RA
          @addConstraint(model, x[k1,k2,i] == 0)
        end
      end
    end
  end

  for k2 in CA
    # Every non-starting vertex has exactly one predecessor.
    @addConstraint(model, sum{x[k1,k2,i], k1=RA, i=VA} == 1)
  end

  FUCKTHIS

  status = solve(model)
  info("Objective: ", getObjectiveValue(model))

  solveroutput = getValue(x)
  routes = []
  for i in VA
    components = Dict()
    for k1 in RA
      for k2 in RA
        if solveroutput[k1,k2,i] == 1
          components[k1] = k2
        end
      end
    end
    position = i
    order = [position]
    while haskey(components, position)
      nextposition = components[position]
      delete!(components, position)
      position = nextposition
      push!(order, position)
    end
    push!(routes, order)
  end
  return routes
end

"""

function optimize(vehicles, commodities, distances, durations, capacities, parameters, objective)
  code = replace(original, "FUCKTHIS", objective)
  return include_string(code)(vehicles, commodities, distances, durations, capacities, parameters)
end
