package xyz.thepathfinder.routing.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import xyz.thepathfinder.routing.domain.RoutingSolution;

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
        return new RoutingSolution();
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
