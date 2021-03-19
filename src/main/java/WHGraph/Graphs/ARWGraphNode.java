package WHGraph.Graphs;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class ARWGraphNode {

    private final int id;
    private Coordinates location;
    private double heuristic;

    private GraphNodeType type;
    private boolean contains_product = false;



    public ARWGraphNode(int id, Coordinates location, GraphNodeType type,
                        List<Edge> neighbours) {

        this.id = id;
        this.location = (Coordinates) location.clone();
        this.type = type;
    }

    public ARWGraphNode(int id) {
        this.id = id;
        this.type = GraphNodeType.SIMPLE;
        this.heuristic = 0;
    }

    public ARWGraphNode(int id, float x, float y, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = new Coordinates(x, y, 0);
        this.heuristic = 0;
    }

    public ARWGraphNode(int id, float x, float y, float z, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = new Coordinates(x, y, z);
        this.heuristic = 0;
    }

    public ARWGraphNode(int id, WHGraph.Graphs.ARWGraphNode node, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = node.getLocation();
        this.heuristic = 0;
    }

    public float getX(){
        return location.getX();
    }

    public float getY(){
        return location.getY();
    }


    public boolean contains_product() {
        return contains_product;
    }

    public void setContains_product(boolean contains_product) {
        if (this.type.equals(GraphNodeType.SIMPLE)) {
            this.contains_product = contains_product;
        }
    }

    @Override
    public WHGraph.Graphs.ARWGraphNode clone() {
        return new WHGraph.Graphs.ARWGraphNode(
                this.id,
                this.location,
                this.type,
                null);
    }

    public GraphNodeType getType() {
        return type;
    }

    public void setType(GraphNodeType type) {
        this.type = type;
    }

    public Coordinates getLocation() {
        return location;
    }


    public void setLocation(Coordinates location) {
        this.location = location;
    }

    private final List<Edge> neighbours = new ArrayList<Edge>();

    public int getGraphNodeId() {
        return this.id;
    }

    public void addNeighbour(Edge e) {
        if (this.neighbours.contains(e)) {
            //System.out.println("This edge has already been used for this node.");
        } else {
            //System.out.println("Successfully added " + e);
            this.neighbours.add(e);
        }
    }

    public List<Edge> getNeighbours() {
        //System.out.println("List of all edges that node " + this.id +" has: ");

        return neighbours;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WHGraph.Graphs.ARWGraphNode) {
            return this.getGraphNodeId() == ((WHGraph.Graphs.ARWGraphNode) obj).getGraphNodeId();
        } else {
            return false;
        }

    }


    public float getDistance(WHGraph.Graphs.ARWGraphNode node) {
        float distance = Math.abs((int) (node.getLocation().getX() - this.getLocation().getX())) + Math.abs((int) (node.getLocation().getY() - this.getLocation().getY()));
        return distance;
    }

    public float getDistance(float x, float y) {
        float distance = Math.abs((int) (x - this.getLocation().getX())) + Math.abs((int) (y - this.getLocation().getY()));
        return distance;
    }


    public List<WHGraph.Graphs.ARWGraphNode> getNeighbourNodes() {
        List<WHGraph.Graphs.ARWGraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < neighbours.size(); i++) {
            if (!neighbours.get(i).getEnd().equals(this)) {
                nodes.add(neighbours.get(i).getEnd());
            } else {
                nodes.add(neighbours.get(i).getStart());
            }
        }
        return nodes;
    }

    public List<WHGraph.Graphs.ARWGraphNode> getVerticalSimpleNode() {
        List<WHGraph.Graphs.ARWGraphNode> list = new ArrayList<>();
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getOtherEnd(this).getType() == GraphNodeType.SIMPLE && neighbours.get(i).getOtherEnd(this).getLocation().getX() == this.getLocation().getX()) {
                list.add(neighbours.get(i).getOtherEnd(this));
            }
        }
        return list;
    }


    @Override
    public String toString() {
        return this.type.toLetter() + "" + this.id + " " +  " Cost " + " " + getType().toString() + " " + this.getLocation().toString() + "\n\t\t";
    }


    public Node generateXMLelement(Document document) {

        Element position = document.createElement("Position");
        Element x = document.createElement("x");
        x.appendChild(document.createTextNode(this.location.getX() + ""));
        position.appendChild(x);
        Element y = document.createElement("y");
        y.appendChild(document.createTextNode(this.location.getY() + ""));
        position.appendChild(y);
        Element z = document.createElement("z");
        z.appendChild(document.createTextNode(this.location.getZ() + ""));
        position.appendChild(z);


        return position;
    }

    public void removeNeighbours() {
        for (Edge neighbour : neighbours) {
            if (this == neighbour.getStart()) {
                neighbour.getEnd().removeNeighbour(this);
            } else {
                neighbour.getStart().removeNeighbour(this);
            }
        }
        neighbours.clear();
    }

    public void removeNeighbour(WHGraph.Graphs.ARWGraphNode ARWGraphNode) {
        List<Edge> toRemove = new ArrayList<>();
        for (Edge neighbour : neighbours) {
            if (neighbour.getEnd().getGraphNodeId() == ARWGraphNode.getGraphNodeId() || neighbour.getStart().getGraphNodeId() == ARWGraphNode.getGraphNodeId()) {
                toRemove.add(neighbour);
            }
        }

        neighbours.removeAll(toRemove);
    }


    public String printName() {
        return this.getType().toLetter() + "" + this.getGraphNodeId();
    }

}
