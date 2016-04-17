package xyz.thepathfinder.routing.domain;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import xyz.thepathfinder.simulatedannealing.InfeasibleProblemException;
import xyz.thepathfinder.simulatedannealing.Problem;

public class VehicleRoutingProblem implements Problem<VRPSearchState> {
    final List<Transport> transports;
    final List<CommodityDropoff> commodityDropoffs;
    final Table<RouteAction, RouteAction, Integer> distances;
    final Table<RouteAction, RouteAction, Integer> durations;
    final Map<RouteAction, Map<String, Integer>> capacities;

    public VehicleRoutingProblem(
        List<Transport> transports,
        List<CommodityDropoff> commodityDropoffs,
        Table<RouteAction, RouteAction, Integer> distances,
        Table<RouteAction, RouteAction, Integer> durations,
        Map<RouteAction, Map<String, Integer>> capacities) {
        this.transports = transports;
        this.commodityDropoffs = commodityDropoffs;
        this.distances = distances;
        this.durations = durations;
        this.capacities = capacities;
    }

    @Override
    public VRPSearchState initialState() throws InfeasibleProblemException {
        Map<Transport, List<CommodityAction>> routes = transports.stream().collect(
            Collectors.toMap(t -> t, t -> new ArrayList<>()));
        List<CommodityDropoff> inTransitCommodities = new ArrayList<>();
        List<CommodityDropoff> waitingCommodities = new ArrayList<>();
        commodityDropoffs.forEach(commodityRequest -> {
            if (commodityRequest.start instanceof Transport) {
                inTransitCommodities.add(commodityRequest);
            } else if (commodityRequest.start instanceof CommodityPickup) {
                waitingCommodities.add(commodityRequest);
            }
        });
        inTransitCommodities.forEach(commodityDropoff -> {
            routes.get(commodityDropoff.start).add(commodityDropoff);
        });
        for (CommodityDropoff commodityDropoff : waitingCommodities) {
            boolean commodityPlaced = false;
            for (Transport transport: transports) {
                List<CommodityAction> transportRoute = new ArrayList<>(routes.get(transport));
                transportRoute.add((CommodityPickup) commodityDropoff.start);
                transportRoute.add(commodityDropoff);
                if (validCapacities(applyCapacities(capacities.get(transport), transportRoute))) {
                    routes.put(transport, transportRoute);
                    commodityPlaced = true;
                    break;
                }
            }
            if (!commodityPlaced) {
                throw new InfeasibleProblemException("Unable to create initial state");
            }
        }
        return new VRPSearchState(this, routes);
    }

    @Override
    public double energy(VRPSearchState searchState) {
        double totalDistance = 0;
        for (Map.Entry<Transport, List<CommodityAction>> route : searchState.routes.entrySet()) {
            Transport transport = route.getKey();
            List<CommodityAction> commodityActions = route.getValue();
            if (!commodityActions.isEmpty()) {
                totalDistance += distances.get(transport, commodityActions.get(0));
            }
            for (int i = 0; i < commodityActions.size() - 1; i++) {
                totalDistance += distances.get(commodityActions.get(i), commodityActions.get(i+1));
            }
        }
        return totalDistance;
    }

    Map<String, Integer> applyCapacities(
        Map<String, Integer> startingCapacities, List<CommodityAction> actions) {
        Map<String, Integer> endingCapacities = new HashMap<>(startingCapacities);
        actions.forEach(action -> {
            capacities.get(action).entrySet().forEach(
                e -> endingCapacities.computeIfPresent(e.getKey(), (k, v) -> v + e.getValue()));
        });
        return endingCapacities;
    }

    private static boolean validCapacities(Map<String, Integer> capacities) {
        return capacities.values().stream().allMatch(c -> c >= 0);
    }
}
