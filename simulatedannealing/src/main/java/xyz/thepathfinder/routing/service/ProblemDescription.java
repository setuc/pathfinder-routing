package xyz.thepathfinder.routing.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.thepathfinder.routing.domain.CommodityDropoff;
import xyz.thepathfinder.routing.domain.CommodityPickup;
import xyz.thepathfinder.routing.domain.CommodityStart;
import xyz.thepathfinder.routing.domain.RouteAction;
import xyz.thepathfinder.routing.domain.Transport;
import xyz.thepathfinder.routing.domain.VehicleRoutingProblem;

import static java.util.stream.Collectors.toMap;

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

    public VehicleRoutingProblem createProblem() {
        Map<Integer, Transport> transportMap = transports.stream().collect(
            toMap(n -> n, n -> new Transport(String.valueOf(n))));
        List<CommodityDropoff> commodityDropoffList = new ArrayList<>();
        Map<Integer, RouteAction> routeActionMap = new HashMap<>();
        routeActionMap.putAll(transportMap);
        for (Map.Entry<Integer, Integer> commodityEntry : commodities.entrySet()) {
            CommodityStart start;
            if (transportMap.containsKey(commodityEntry.getKey())) {
                start = transportMap.get(commodityEntry.getKey());
            } else {
                int pickupId = commodityEntry.getValue();
                start = new CommodityPickup(String.valueOf(pickupId));
                routeActionMap.put(pickupId, (CommodityPickup) start);
            }
            int dropoffId = commodityEntry.getKey();
            CommodityDropoff dropoff = new CommodityDropoff(String.valueOf(dropoffId), start);
            commodityDropoffList.add(dropoff);
            routeActionMap.put(dropoffId, dropoff);
        }
        Table<RouteAction, RouteAction, Integer> distanceTable = HashBasedTable.create();
        Table<RouteAction, RouteAction, Integer> durationTable = HashBasedTable.create();
        for (int r = 0; r < distances.length; r++) {
            for (int c = 0; c < distances[r].length; c++) {
                if (routeActionMap.containsKey(r+1) && routeActionMap.containsKey(c+1)) {
                    RouteAction ra1 = routeActionMap.get(r+1);
                    RouteAction ra2 = routeActionMap.get(c+1);
                    distanceTable.put(ra1, ra2, distances[r][c]);
                    distanceTable.put(ra2, ra1, distances[r][c]);
                    durationTable.put(ra1, ra2, durations[r][c]);
                    durationTable.put(ra2, ra1, durations[r][c]);
                }
            }
        }

        Map<RouteAction, Map<String, Integer>> allCapacities =
            routeActionMap.values().stream().collect(toMap(a -> a, a -> new HashMap<>()));
        for (Map.Entry<String, Map<Integer, Integer>> e1 : capacities.entrySet()) {
            for (Map.Entry<Integer, Integer> e2 : e1.getValue().entrySet()) {
                RouteAction routeAction = routeActionMap.get(e2.getKey());
                // Dan sends negative for dropoff, positive for pickup.
                int value = routeAction instanceof Transport ? e2.getValue() : -1 * e2.getValue();
                allCapacities.get(routeAction).put(e1.getKey(), value);
            }
        }

        return new VehicleRoutingProblem(
            new ArrayList<Transport>(transportMap.values()),
            commodityDropoffList,
            distanceTable,
            durationTable,
            allCapacities);
    }
}
