package xyz.thepathfinder.routing.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import xyz.thepathfinder.simulatedannealing.ExponentialDecayScheduler;
import xyz.thepathfinder.simulatedannealing.InfeasibleProblemException;
import xyz.thepathfinder.simulatedannealing.LinearDecayScheduler;
import xyz.thepathfinder.simulatedannealing.Problem;
import xyz.thepathfinder.simulatedannealing.Scheduler;
import xyz.thepathfinder.simulatedannealing.Solver;

public class Main {
    static Map<RouteAction, Location> locations = new HashMap<>();

    public static void main(String args[]) throws InfeasibleProblemException {
        Problem<VRPSearchState> problem = createProblem(5, 5);

        solveAndPrint(problem, new LinearDecayScheduler(10000, 500));
        solveAndPrint(problem, new LinearDecayScheduler(10000, 500));
        solveAndPrint(problem, new LinearDecayScheduler(10000, 500));
        solveAndPrint(problem, new LinearDecayScheduler(10000, 500000));
        solveAndPrint(problem, new ExponentialDecayScheduler(10000, 500000));
    }

    static void solveAndPrint(Problem<VRPSearchState> problem, Scheduler scheduler)
        throws InfeasibleProblemException {
        Solver<VRPSearchState> solver = new Solver(problem, scheduler);
        VRPSearchState solution = solver.solve();
        printResults(problem, solution);
    }

    static void printResults(Problem<VRPSearchState> problem, VRPSearchState solution) {
        StringBuilder builder = new StringBuilder();
        builder.append("Distance: ")
            .append(problem.energy(solution))
            .append("\n");
        for (Map.Entry<Transport, List<CommodityAction>> entry: solution.routes.entrySet()) {
            builder.append(entry.getKey().getName())
                .append(" - ")
                .append(locations.get(entry.getKey()))
                .append("\n");
            for (CommodityAction action : entry.getValue()) {
                builder.append("\t")
                    .append(action.getName())
                    .append(" - ")
                    .append(locations.get(action))
                    .append("\n");
            }
        }
        System.out.println(builder.toString());
    }

    static VehicleRoutingProblem createProblem(int numTransports, int numCommodities) {

        List<Transport> transports = new ArrayList<>();
        Map<RouteAction, Map<String, Integer>> capacities = new HashMap<>();
        for (int i = 0; i < numTransports; i++) {
            Transport transport = new Transport("Transport" + i);
            locations.put(transport, Location.randomLocation());
            transports.add(transport);
            capacities.put(transport, ImmutableMap.of("chimney", 5));
        }
        List<CommodityDropoff> commodityDropoffs = new ArrayList<>();
        for (int i = 0; i < numCommodities; i++) {
            CommodityPickup pickup = new CommodityPickup("Pickup" + i);
            CommodityDropoff dropoff = new CommodityDropoff("Dropoff" + i, pickup);
            locations.put(pickup, Location.randomLocation());
            locations.put(dropoff, Location.randomLocation());
            commodityDropoffs.add(dropoff);
            capacities.put(pickup, ImmutableMap.of("chimney", -1));
            capacities.put(dropoff, ImmutableMap.of("chimney", 1));
        }
        Table<RouteAction, RouteAction, Integer> distances = HashBasedTable.create();
        for (int t1 = 0; t1 < numTransports; t1++) {
            Transport transport1 = transports.get(t1);
            for (int t2 = t1 + 1; t2 < numTransports; t2++) {
                Transport transport2 = transports.get(t2);
                int distance = distance(locations.get(transport1), locations.get(transport2));
                distances.put(transport1, transport2, distance);
                distances.put(transport2, transport1, distance);
            }
            for (int c = 0; c < numCommodities; c++) {
                CommodityStart start = commodityDropoffs.get(c).start;
                CommodityDropoff dropoff = commodityDropoffs.get(c);
                int startDistance = distance(locations.get(transport1), locations.get(start));
                int dropoffDistance = distance(locations.get(transport1), locations.get(dropoff));
                distances.put(transport1, start, startDistance);
                distances.put(start, transport1, startDistance);
                distances.put(transport1, dropoff, dropoffDistance);
                distances.put(dropoff, transport1, dropoffDistance);
            }
        }
        for (int c1 = 0; c1 < numCommodities; c1++) {
            CommodityStart start1 = commodityDropoffs.get(c1).start;
            CommodityDropoff dropoff1 = commodityDropoffs.get(c1);
            int directDistance = distance(locations.get(start1), locations.get(dropoff1));
            distances.put(start1, dropoff1, directDistance);
            distances.put(dropoff1, start1, directDistance);
            for (int c2 = c1; c2 < numCommodities; c2++) {
                CommodityStart start2 = commodityDropoffs.get(c2).start;
                CommodityDropoff dropoff2 = commodityDropoffs.get(c2);
                int startStart = distance(locations.get(start1), locations.get(start2));
                distances.put(start1, start2, startStart);
                distances.put(start2, start1, startStart);
                int startDrop = distance(locations.get(start1), locations.get(dropoff2));
                distances.put(start1, dropoff2, startDrop);
                distances.put(dropoff2, start1, startDrop);
                int dropStart = distance(locations.get(dropoff1), locations.get(start2));
                distances.put(dropoff1, start2, dropStart);
                distances.put(start2, dropoff1, dropStart);
                int dropDrop = distance(locations.get(dropoff1), locations.get(dropoff2));
                distances.put(dropoff1, dropoff2, dropDrop);
                distances.put(dropoff2, dropoff1, dropDrop);
            }
        }
        Table<RouteAction, RouteAction, Integer> durations = HashBasedTable.create();
        return new VehicleRoutingProblem(transports, commodityDropoffs, distances, durations, capacities);
    }

    static int distance(Location a, Location b) {
        return (int) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    static class Location {
        private static final Random random = new Random();
        private static final DecimalFormat df = new DecimalFormat("#.##");
        final double x;
        final double y;

        Location(double x, double y) {
            this.x = x;
            this.y = y;
        }

        static Location randomLocation() {
            // Approximates size of san francisco.
            return new Location(random.nextDouble() * 24494, random.nextDouble() * 24494);
        }

        @Override
        public String toString() {
            return "(" + df.format(x) + ", " + df.format(y) + ")";
        }
    }
}
