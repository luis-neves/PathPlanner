package utils.Graphs;

import armazem.Cell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import utils.warehouse.Coordenates;

import java.util.*;

public class GraphNode {
    private int id;
    private Coordenates location;
    private double heuristic;
    private GraphNode parent;
    private GraphNodeType type;
    private float weightPhysical;
    
    public float getWeightPhysical() {
        if (this.getType() == GraphNodeType.PRODUCT) {
            return weightPhysical;
        } else {
            return -1;
        }
    }

    public void setWeightPhysical(float weightPhysical) {
        this.weightPhysical = weightPhysical;
    }

    public GraphNodeType getType() {
        return type;
    }

    public void setType(GraphNodeType type) {
        this.type = type;
    }

    private int f;

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    private int g;

    public Coordenates getLocation() {
        return location;
    }

    public void setLocation(Coordenates location) {
        this.location = location;
    }

    private List<Edge> neighbours = new ArrayList<Edge>();

    public int getGraphNodeId() {
        return this.id;
    }

    public void addNeighbour(Edge e) {
        if (this.neighbours.contains(e)) {
            System.out.println("This edge has already been used for this node.");
        } else {
            //System.out.println("Successfully added " + e);
            this.neighbours.add(e);
        }
    }

    public List<Edge> getNeighbours() {
        /*System.out.println("List of all edges that node " + this.id +" has: ");
        System.out.println("=================================");
        for (int i = 0; i < this.neighbours.size(); i++ ){
            System.out.println("ID of Edge: " + neighbours.get(i).getId() + "\nID of the first node: " + neighbours.get(i).getIdOfStartGraphNode() +
                    "\nID of the second node: " + neighbours.get(i).getIdOfEndGraphNode());
            System.out.println();
        }
        System.out.println(neighbours);*/
        return neighbours;
    }

    public double getNodeWeight(GraphNode node) {
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getEnd().getGraphNodeId() == node.getGraphNodeId()) {
                return neighbours.get(i).getWeight();
            }
            if (neighbours.get(i).getStart().getGraphNodeId() == node.getGraphNodeId()) {
                return neighbours.get(i).getWeight();
            }
        }
        return Double.MAX_VALUE;
    }


    public GraphNode(int id) {
        this.id = id;
        this.type = GraphNodeType.SIMPLE;
        this.f = 0;
        this.heuristic = 0;
    }

    public void calculateHeuristic(GraphNode finalGraphNode) {
        int distance = Math.abs((int) (finalGraphNode.getLocation().getX() - this.getLocation().getX())) + Math.abs((int) (finalGraphNode.getLocation().getY() - this.getLocation().getY()));
        this.heuristic = distance;
    }

    public float getDistance(GraphNode node) {
        float distance = Math.abs((int) (node.getLocation().getX() - this.getLocation().getX())) + Math.abs((int) (node.getLocation().getY() - this.getLocation().getY()));
        return distance;
    }

    public GraphNode getParent() {
        return this.parent;
    }

    public List<GraphNode> getNeighbourNodes() {
        List<GraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < neighbours.size(); i++) {
            if (!neighbours.get(i).getEnd().equals(this)) {
                nodes.add(neighbours.get(i).getEnd());
            } else {
                nodes.add(neighbours.get(i).getStart());
            }
        }
        return nodes;
    }

    public List<GraphNode> getNeighbourNodesWithoutProducts(List<GraphNode> taskedNodes) {
        List<GraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < neighbours.size(); i++) {
            if (!neighbours.get(i).getEnd().equals(this)) {
                if (taskedNodes.contains(neighbours.get(i).getEnd())) {
                    nodes.add(neighbours.get(i).getEnd());
                } else if (neighbours.get(i).getEnd().getType() != GraphNodeType.PRODUCT && neighbours.get(i).getEnd().getType() != GraphNodeType.AGENT) {
                    nodes.add(neighbours.get(i).getEnd());
                }
            } else if (!neighbours.get(i).getStart().equals(this)) {
                if (taskedNodes.contains(neighbours.get(i).getStart())) {
                    nodes.add(neighbours.get(i).getStart());
                } else if (neighbours.get(i).getStart().getType() != GraphNodeType.PRODUCT && neighbours.get(i).getStart().getType() != GraphNodeType.AGENT) {
                    nodes.add(neighbours.get(i).getStart());
                }
            }
        }
        return nodes;
    }

    public int getHeuristic() {
        return (int) Math.round(heuristic);
    }

    public void setNodeData(GraphNode currentNode, double cost) {
        int gCost = (int) (currentNode.getG() + cost);
        setParent(currentNode);
        setG(gCost);
        //System.out.println(this.toString());
        calculateFinalCost();
    }

    private void calculateFinalCost() {
        int finalCost = (int) (getG() + getHeuristica());
        //System.out.println("FINAL COST" + finalCost);
        setF(finalCost);
    }

    private double getHeuristica() {
        return this.heuristic;
    }

    private void setParent(GraphNode currentNode) {
        this.parent = currentNode;
    }

    public boolean checkBetterPath(GraphNode currentNode) {
        double cost = 0;
        setG((int) getNodeWeight(currentNode));
        int gCost = (int) (currentNode.getG() + cost);
        if (gCost < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    public String getNeighboursStr() {
        String neighbours = "";
        for (int i = 0; i < this.neighbours.size(); i++) {
            neighbours += this.neighbours.get(i).toString() + "\n\t\t";
        }
        return neighbours;
    }

    @Override
    public String toString() {
        return this.type.toLetter() + "" + this.id + " " + this.getHeuristica() + " Cost " + this.getF() + " " + this.getLocation().toString() + " " + getType().toString() + "\n\t\t" + getNeighboursStr();
    }

    public Edge getNeighbourEdge(GraphNode graphNode) {
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getEnd().equals(graphNode) || neighbours.get(i).getStart().equals(graphNode)) {
                return neighbours.get(i);
            }
        }
        return null;
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

    public void removeNeighbour(GraphNode graphNode) {
        List<Edge> toRemove = new ArrayList<>();
        for (Edge neighbour : neighbours) {
            if (neighbour.getEnd().getGraphNodeId() == graphNode.getGraphNodeId() || neighbour.getStart().getGraphNodeId() == graphNode.getGraphNodeId()) {
                toRemove.add(neighbour);
            }
        }
        //System.out.println("Removed " + toRemove.size());
        neighbours.removeAll(toRemove);
    }

    public void removeSouthSimple() {
        GraphNode toRemove = null;
        Edge toRemoveE = null;
        for (int i = 0; i < neighbours.size(); i++) {
            GraphNode neighbor = null;
            if (neighbours.get(i).getStart().getGraphNodeId() != this.getGraphNodeId()) {
                neighbor = neighbours.get(i).getStart();
            } else {
                neighbor = neighbours.get(i).getEnd();
            }
            if (neighbor.getLocation().getX() == this.getLocation().getX() && neighbor.getType() != GraphNodeType.PRODUCT) {
                toRemove = neighbor;
                toRemoveE = neighbours.get(i);
            }
        }
        if (toRemove != null) {
            neighbours.remove(toRemoveE);
            toRemove.removeNeighbour(this);
            neighbours.remove(toRemove);
        }
    }
}
