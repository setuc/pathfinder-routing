package xyz.thepathfinder.routing;

import com.google.common.collect.ImmutableMap;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.util.Arrays;
import java.util.List;

import xyz.thepathfinder.routing.domain.CommodityAction;
import xyz.thepathfinder.routing.domain.CommodityDropoff;
import xyz.thepathfinder.routing.domain.CommodityPickup;
import xyz.thepathfinder.routing.domain.RoutingSolution;
import xyz.thepathfinder.routing.domain.Transport;

public class Main {

    public static final String SOLVER_CONFIG = "xyz/thepathfinder/routing/solverconfig.xml";

    public static void main(String args[]) {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver solver = solverFactory.buildSolver();
        RoutingSolution routingSolution = new RoutingSolution();
        Transport transport1 = new Transport(1);
        Transport transport2 = new Transport(2);
        List<Transport> transports = Arrays.asList(
            transport1,
            transport2
        );
        CommodityPickup commodityAction1 = new CommodityPickup(3);
        CommodityDropoff commodityAction2 = new CommodityDropoff(4);
        List<CommodityAction> commodityActions = Arrays.asList(
            commodityAction1,
            commodityAction2
        );
        commodityAction1.setDistances(ImmutableMap.of(
            commodityAction2, 2000L,
            transport1, 2828L,
            transport2, 4472L
        ));
        commodityAction2.setDistances(ImmutableMap.of(
            commodityAction1, 2000L,
            transport1, 2000L,
            transport2, 4000L
        ));
        commodityAction1.setDropoff(commodityAction2);
        commodityAction2.setPickup(commodityAction1);
        routingSolution.setTransportList(transports);
        routingSolution.setCommodityActionList(commodityActions);
        System.out.println("Calling solve");
        solver.solve(routingSolution);
        System.out.println("Finished computing solution");
        System.out.println(commodityAction1.getTransport());
        System.out.println(commodityAction2.getTransport());
        System.out.println(commodityAction1.getPreviousRouteAction());
        System.out.println(commodityAction2.getPreviousRouteAction());
        System.out.println(transport1.getNextCommodityAction());
        System.out.println(transport2.getNextCommodityAction());
        RoutingSolution solution = (RoutingSolution) solver.getBestSolution();
        solution.getTransportList().forEach(t -> {
            System.out.println(t);
            CommodityAction nextAction = t.getNextCommodityAction();
            while (nextAction != null) {
                System.out.println(nextAction);
                nextAction = nextAction.getNextCommodityAction();
            }
        });
    }
}
