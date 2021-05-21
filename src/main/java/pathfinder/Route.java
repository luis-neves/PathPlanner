package pathfinder;


import java.util.List;

public class Route {
    private final List route;
    private final double cost;

    public Route(List route, double cost){
        this.route=route;
        this.cost=cost;
    }

    public List getRoute() {
        return route;
    }

    public double getCost() {
        return cost;
    }


}
