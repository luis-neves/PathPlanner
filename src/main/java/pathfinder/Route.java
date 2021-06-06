package pathfinder;


import java.util.List;

public class Route {
    private final List nodes;
    private final double cost;

    public Route(List nodes, double cost){
        this.nodes = nodes;
        this.cost = cost;
    }

    public List getNodes() {
        return nodes;
    }

    public double getCost() {
        return cost;
    }

}
