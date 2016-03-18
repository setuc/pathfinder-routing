package xyz.thepathfinder.routing.service;

import java.util.List;
import java.util.Map;

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

    @Override
    public String toString() {
        return objective;
    }
}
