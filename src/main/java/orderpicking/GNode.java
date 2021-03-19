package orderpicking;

import pathfinder.GraphNode;

import java.util.StringJoiner;

public class GNode implements GraphNode {
    private final String id;
    private final String name;
    private final double x;
    private final double y;

    public GNode(String id, String name, double x, double y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", orderpicking.GNode.class.getSimpleName() + "[", "]").add("id='" + id + "'").toString();
               // .add("name='" + name + "'").add("latitude=" + x).add("longitude=" + y).toString();
    }
}