package utils.Graphs;

public class FitnessNode {
    private int id;
    private GraphNode node;
    private Float cost;

    public FitnessNode(int id, GraphNode node, Float cost, Float time) {
        this.id = id;
        this.node = node;
        this.cost = cost;
        this.time = time;
    }

    @Override
    public Object clone() {
        return new FitnessNode(this.id, (GraphNode) this.node.clone(), this.cost, this.time);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GraphNode getNode() {
        return node;
    }

    public void setNode(GraphNode node) {
        this.node = node;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    private Float time;
}
