package xyz.thepathfinder.routing.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import xyz.thepathfinder.simulatedannealing.SearchState;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class VRPSearchState implements SearchState<VRPSearchState> {
    final static int MAX_STEP_TRIES = 5;
    final VehicleRoutingProblem problem;
    final Map<Transport, List<CommodityAction>> routes;
    final Map<Transport, List<Map<String, Integer>>> capacitiesByTransportByIndex;
    static final Random random = new Random();

    VRPSearchState(VehicleRoutingProblem problem, Map<Transport, List<CommodityAction>> routes) {
        this.problem = problem;
        this.routes = routes;
        capacitiesByTransportByIndex = new HashMap<>();
    }

    public Map<Transport, List<CommodityAction>> getRoutes() {
        return routes;
    }

    @Override
    public VRPSearchState step() {
        for (int attempts = MAX_STEP_TRIES; attempts --> 0; ) {
            Optional<VRPSearchState> neighbor =
                random.nextBoolean() ? swapBetweenRoutes() : swapWithinRoute();
            if (neighbor.isPresent()) {
                return neighbor.get();
            }
        }
        return this;
    }

    // Attempts to swap. Probably fails.
    public Optional<VRPSearchState> swapWithinRoute() {
        List<Transport> candidates = routes.keySet().stream()
            .filter(this::canSwapWithinRoute)
            .filter(this::hasSwapCandidates)
            .collect(toList());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        Transport selectedTransport = candidates.get(random.nextInt(candidates.size()));
        List<CommodityAction> route = routes.get(selectedTransport);
        List<Map<String, Integer>> capacities = capacitiesByIndex(selectedTransport);

        // Randomly select a pair of adjacent actions to swap, (index, index + 1).
        List<Integer> swapCandidates = IntStream.range(0, route.size() - 1)
            .filter(index -> !arePickupDropffPair(route.get(index), route.get(index+1)))
            .boxed()
            .collect(toList());
        int index = swapCandidates.get(random.nextInt(swapCandidates.size()));


        Map<String, Integer> oldCapacities = capacities.get(index+1);
        // Ask Adam how this works if you're curious.
        Map<String, Integer> newCapacities = oldCapacities.keySet().stream()
            .collect(toMap(k -> k, k -> capacities.get(index).get(k) - oldCapacities.get(k) + capacities.get(index+2).get(k)));
        if (newCapacities.values().stream().allMatch(x -> x >= 0)) {
            // This is a legal swap!
            List<CommodityAction> newRoute = new ArrayList<>(route);
            CommodityAction swappedAction = newRoute.get(index);
            newRoute.set(index, newRoute.get(index+1));
            newRoute.set(index+1, swappedAction);
            Map<Transport, List<CommodityAction>> newRoutes = new HashMap<>(routes);
            newRoutes.put(selectedTransport, newRoute);

            return Optional.of(new VRPSearchState(problem, newRoutes));
        }
        return Optional.empty();
    }

    private boolean hasSwapCandidates(Transport transport) {
        List<CommodityAction> route = routes.get(transport);
        return IntStream.range(0, route.size() - 1)
            .anyMatch(index -> !arePickupDropffPair(route.get(index), route.get(index+1)));
    }

    // Attempts to swap. Probably fails.
    public Optional<VRPSearchState> swapBetweenRoutes() {
        List<Transport> candidates =
            routes.keySet().stream().filter(t -> routes.get(t).size() > 1).collect(toList());
        Transport firstTransport = candidates.get(random.nextInt(candidates.size()));
        List<CommodityAction> firstRoute = routes.get(firstTransport);
        List<CommodityAction> potentialMovers = firstRoute.stream()
            .filter(ca -> ca instanceof CommodityDropoff && ((CommodityDropoff) ca).start instanceof CommodityPickup)
            .collect(toList());
        if (potentialMovers.isEmpty()) {
            return Optional.empty();
        }
        CommodityDropoff mover =
            (CommodityDropoff) potentialMovers.get(random.nextInt(potentialMovers.size()));
        List<Transport> secondTransportCandidates = routes.keySet().stream()
            .filter(t -> !t.equals(firstTransport) && canFitInFront(t, mover))
            .collect(toList());
        if (secondTransportCandidates.isEmpty()) {
            return Optional.empty();
        }
        Transport secondTransport =
            secondTransportCandidates.get(random.nextInt(secondTransportCandidates.size()));
        List<CommodityAction> newFirstRoute = new ArrayList<>(firstRoute);
        newFirstRoute.remove(mover);
        newFirstRoute.remove(mover.start);
        LinkedList<CommodityAction> newSecondRoute = new LinkedList<>(routes.get(secondTransport));
        newSecondRoute.push(mover);
        newSecondRoute.push((CommodityPickup) mover.start);
        Map<Transport, List<CommodityAction>> newRoutes = new HashMap<>(routes);
        newRoutes.put(firstTransport, newFirstRoute);
        newRoutes.put(secondTransport, newSecondRoute);
        return Optional.of(new VRPSearchState(problem, newRoutes));
    }

    boolean canFitInFront(Transport transport, CommodityDropoff dropoff) {
        Map<String, Integer> transportCapacity = problem.capacities.get(transport);
        Map<String, Integer> pickupCapacity = problem.capacities.get(dropoff.start);
        for (String key : transportCapacity.keySet()) {
            if (transportCapacity.get(key) < pickupCapacity.get(key)) {
                return false;
            }
        }
        return true;
    }

    boolean arePickupDropffPair(CommodityAction pickup, CommodityAction dropoff) {
        return dropoff instanceof CommodityDropoff && ((CommodityDropoff) dropoff).start.equals(pickup);
    }

    boolean canSwapWithinRoute(Transport transport) {
        return routes.get(transport).size() > 1;
    }

    List<Map<String, Integer>> capacitiesByIndex(Transport transport) {
        if (!capacitiesByTransportByIndex.containsKey(transport)) {
            capacitiesByTransportByIndex.put(transport, computeCapacitiesByIndex(transport));
        }
        return capacitiesByTransportByIndex.get(transport);
    }

    private List<Map<String, Integer>> computeCapacitiesByIndex(Transport transport) {
        List<Map<String, Integer>> result = new ArrayList<>();
        List<CommodityAction> route = routes.get(transport);
        Map<String, Integer> current = problem.capacities.get(transport);
        result.add(current);
        for (int i = 0; i < route.size(); i++) {
            Map<String, Integer> next = new HashMap<>(current);
            for (Map.Entry<String, Integer> difference : problem.capacities.get(route.get(i)).entrySet()) {
                next.put(difference.getKey(), next.get(difference.getKey()) + difference.getValue());
            }
            result.add(next);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Transport, List<CommodityAction>> entry: routes.entrySet()) {
            builder.append(entry.getKey()).append("\n");
            for (CommodityAction action : entry.getValue()) {
                builder.append("\t- ").append(action).append("\n");
            }
        }
        return builder.toString();
    }
}
