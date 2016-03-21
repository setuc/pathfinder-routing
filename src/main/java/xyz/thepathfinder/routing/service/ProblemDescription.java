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
    private List<Integer> vehicles;
    private Map<Integer, Integer> commodities;
    private Integer[][] durations;
    private Integer[][] distances;
    private Map<String, Map<Integer, Integer>> capacities;
    private Map<String, Map<Integer, Integer>> parameters;
    private String objective;

    public void setVehicles(List<Integer> vehicles) {
        this.vehicles = vehicles;
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

    public List<Integer> getVehicles() {
        return vehicles;
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
        // Initialize transports and commodities
        Map<Integer, Transport> transportMap = vehicles.stream().collect(Collectors.toMap(identity(), Transport::new));
        Map<Integer, CommodityAction> commodityActions = new HashMap<>();
        for (Map.Entry<Integer, Integer> commodityEntry : commodities.entrySet()) {
            CommodityDropoff dropoff = new CommodityDropoff(commodityEntry.getKey());
            commodityActions.put(commodityEntry.getKey(), dropoff);
            if (transportMap.containsKey(commodityEntry.getKey())) {
                dropoff.setPickup(transportMap.get(commodityEntry.getKey()));
            } else {
                CommodityPickup pickup = new CommodityPickup(commodityEntry.getValue());
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
            for (int c = 0; c < distances.length; c++) {
                if (distances[r][c] > 0) {
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
            .append("Vehicles: ").append(vehicles).append("\n")
            .append("Commodities: " ).append(commodities).append("\n")
            .append("Distances: ").append(Arrays.deepToString(distances)).append("\n")
            .append("Durations: ").append(Arrays.deepToString(durations)).append("\n")
            .append("Capacities: ").append(capacities).append("\n")
            .append("Parameters: ").append(parameters).append("\n")
            .append("Objective: ").append(objective)
            .toString();
    }
}
