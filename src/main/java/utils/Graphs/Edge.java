package utils.Graphs;

import org.jfree.data.time.TimePeriodValues;
import utils.warehouse.Coordenates;

import java.util.ArrayList;
import java.util.List;

public class Edge {
    private boolean horizontal_edge = false;
    private boolean vertical_edge = false;
    private GraphNode start;
    private GraphNode end;
    private double weight;
    private boolean product_line;
    private Coordenates location;
    private int num_directions = 1;
    private List<GraphNode> products;

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

        this.products = new ArrayList<>();
        this.start = s;
        this.end = e;
        this.weight = w;
        float x1 = s.getLocation().getX();
        float x2 = e.getLocation().getX();
        float y1 = s.getLocation().getY();
        float y2 = e.getLocation().getY();
        if (x1 == x2)
            this.horizontal_edge = true;
        if (y1 == y2)
            this.horizontal_edge = false;
        this.location = new Coordenates(x1 == x2 ? x2 : (x1 + x2) / 2, y1 == y2 ? y2 : (y1 + y2) / 2, 0);
        this.id = id;
        this.product_line = false;
    }

    public boolean isProduct_line() {
        return product_line;
    }

    public void setProduct_line(boolean product_line) {
        this.product_line = product_line;
    }

    public Edge(GraphNode s, GraphNode e, double w, int id, boolean product_line) {
        this.products = new ArrayList<>();
        this.start = s;
        this.end = e;
        this.weight = w;
        float x1 = s.getLocation().getX();
        float x2 = e.getLocation().getX();
        float y1 = s.getLocation().getY();
        float y2 = e.getLocation().getY();
        if (x1 == x2)
            this.horizontal_edge = true;
        if (y1 == y2)
            this.horizontal_edge = false;
        this.location = new Coordenates(x1 == x2 ? x2 : (x1 + x2) / 2, y1 == y2 ? y2 : (y1 + y2) / 2, 0);
        this.id = id;
        this.product_line = product_line;
    }

    @Override
    public String toString() {
        return "Edge " + id + " W " + getWeight() + " Start " + getStart().getGraphNodeId() + " End " + getEnd().getGraphNodeId();
    }

    public GraphNode getOtherEnd(GraphNode graphNode) {
        if (this.getEnd().getGraphNodeId() == graphNode.getGraphNodeId()) {
            return this.getStart();
        } else {
            return this.getEnd();
        }
    }

    public void addProduct(GraphNode product_agent) {
        this.products.add(product_agent);
    }

    public List<GraphNode> getProducts() {
        return products;
    }
}