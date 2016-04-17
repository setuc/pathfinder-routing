package xyz.thepathfinder.routing.score;

import java.util.Comparator;

import xyz.thepathfinder.routing.domain.CommodityAction;
import xyz.thepathfinder.routing.domain.CommodityDropoff;
import xyz.thepathfinder.routing.domain.CommodityPickup;
import xyz.thepathfinder.routing.domain.RouteAction;

public class NaiveDifficultyComparator implements Comparator<CommodityAction> {

    @Override
    public int compare(CommodityAction o1, CommodityAction o2) {
        if (o1 instanceof CommodityDropoff && o2 instanceof CommodityPickup) {
            return 1;
        } else if (o1 instanceof CommodityPickup && o2 instanceof CommodityDropoff) {
            return -1;
        } else {
            return 0;
        }
    }
}
