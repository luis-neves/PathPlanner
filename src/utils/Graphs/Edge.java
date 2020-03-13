package utils.Graphs;

import utils.warehouse.Coordenates;

public class Edge {
    private GraphNode start;
    private GraphNode end;
    private double weight;
    private Coordenates location;
    private int num_directions = 1;

    public int getNum_directions() {
        return num_directions;
    }

    public void setNum_directions(int num_directions) {
        this.num_directions = num_directions;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordenates getLocation() {
        return location;
    }

    public void setLocation(Coordenates location) {
        this.location = location;
    }

    private int id;
    public int getId() {
        return this.id;
    }

    public void setStart(GraphNode start) {
        this.start = start;
    }

    public void setEnd(GraphNode end) {
        this.end = end;
    }

    public GraphNode getStart() {
        return this.start;
    }
    public int getIdOfStartGraphNode() {
        return this.start.getGraphNodeId();
    }
    public GraphNode getEnd() {
        return this.end;
    }
    public int getIdOfEndGraphNode() {
        return this.end.getGraphNodeId();
    }

    public double getWeight() {
        return this.weight;
    }
    public Edge(GraphNode s, GraphNode e, double w, int id) {
        this.start = s;
        this.end = e;
        this.weight = w;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Edge " + id + " W " + getWeight() + " Start " + getStart().getGraphNodeId() + " End " + getEnd().getGraphNodeId();
    }
}