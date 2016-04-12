package xyz.thepathfinder.routing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import xyz.thepathfinder.routing.domain.CommodityAction;
import xyz.thepathfinder.routing.domain.CommodityDropoff;
import xyz.thepathfinder.routing.domain.CommodityPickup;
import xyz.thepathfinder.routing.domain.RouteAction;
import xyz.thepathfinder.routing.domain.RoutingSolution;
import xyz.thepathfinder.routing.domain.Transport;

import static java.util.function.Function.identity;

public class ProblemDescription {
    private List<Integer> transports;
    private Map<Integer, Integer> commodities;
    private Integer[][] durations;
    private Integer[][] distances;
    private Map<String, Map<Integer, Integer>> capacities;
    private Map<String, Map<Integer, Integer>> parameters;
    private String objective;

    public void setTransports(List<Integer> transports) {
        this.transports = transports;
    }

    public void setCommodities(Map<Integer, Integer> commodities) {
        this.commodities = commodities;
    }

    public void setDurations(Integer[][] durations) {
        this.durations = durations;
    }

    public void setDistances(Integer[][] distances) {
        this.distances = distances;
    }

    public void setCapacities(Map<String, Map<Integer, Integer>> capacities) {
        this.capacities = capacities;
    }

    public void setParameters(Map<String, Map<Integer, Integer>> parameters) {
        this.parameters = parameters;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public List<Integer> getTransports() {
        return transports;
    }

    public Map<Integer, Integer> getCommodities() {
        return commodities;
    }

    public Integer[][] getDurations() {
        return durations;
    }

    public Integer[][] getDistances() {
        return distances;
    }

    public Map<String, Map<Integer, Integer>> getCapacities() {
        return capacities;
    }

    public Map<String, Map<Integer, Integer>> getParameters() {
        return parameters;
    }

    public String getObjective() {
        return objective;
    }

    public RoutingSolution createEmptyRoutingSolution() {
        // Flip the capacities array
        Map<Integer, Map<String, Integer>> flippedCapacities = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Integer>> capacityListEntry : capacities.entrySet()) {
            for (Map.Entry<Integer, Integer> capacityEntry : capacityListEntry.getValue().entrySet()) {
                flippedCapacities.putIfAbsent(capacityEntry.getKey(), new HashMap<>());
                flippedCapacities.get(capacityEntry.getKey()).put(capacityListEntry.getKey(), capacityEntry.getValue());
            }
        }

        // Initialize transports and commodities
        Map<Integer, Transport> transportMap = transports.stream().collect(
            Collectors.toMap(identity(), v -> new Transport(v, flippedCapacities.get(v))));
        Map<Integer, CommodityAction> commodityActions = new HashMap<>();
        for (Map.Entry<Integer, Integer> commodityEntry : commodities.entrySet()) {
            int dropoffId = commodityEntry.getKey();
            CommodityDropoff dropoff = new CommodityDropoff(dropoffId, flippedCapacities.get(dropoffId));
            commodityActions.put(commodityEntry.getKey(), dropoff);
            if (transportMap.containsKey(commodityEntry.getKey())) {
                dropoff.setPickup(transportMap.get(commodityEntry.getKey()));
            } else {
                int pickupId = commodityEntry.getValue();
                CommodityPickup pickup = new CommodityPickup(pickupId, flippedCapacities.get(pickupId));
                commodityActions.put(commodityEntry.getValue(), pickup);
                dropoff.setPickup(pickup);
                pickup.setDropoff(dropoff);
            }
        }

        // Set distance lists for route actions
        Map<Integer, RouteAction> routeActions = new HashMap<>();
        routeActions.putAll(transportMap);
        routeActions.putAll(commodityActions);
        for (int r = 0; r < distances.length; r++) {
            for (int c = 0; c < distances[r].length; c++) {
                if (r != c && routeActions.containsKey(r+1) && routeActions.containsKey(c+1)) {
                    System.out.println("Getting distance["+r+","+c+"]");
                    routeActions.get(r+1).setDistance(routeActions.get(c+1), distances[r][c]);
                }
            }
        }

        // Construct solution
        RoutingSolution solution = new RoutingSolution();
        solution.setCommodityActionList(new ArrayList<>(commodityActions.values()));
        solution.setTransportList(new ArrayList<>(transportMap.values()));
        return solution;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("Transports: ").append(transports).append("\n")
            .append("Commodities: " ).append(commodities).append("\n")
            .append("Distances: ").append(Arrays.deepToString(distances)).append("\n")
            .append("Durations: ").append(Arrays.deepToString(durations)).append("\n")
            .append("Capacities: ").append(capacities).append("\n")
            .append("Parameters: ").append(parameters).append("\n")
            .append("Objective: ").append(objective)
            .toString();
    }
}
