package utils.Graphs;

import armazem.Cell;
import communication.Operator;
import ga.KMeans.MyCluster;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import system.Exception;
import utils.warehouse.Coordenates;
import weka.core.Attribute;

import javax.xml.stream.Location;
import java.util.*;

public class GraphNode {
    private float amplify = 1;
    private int id;
    private Coordenates location;
    private double heuristic;
    private GraphNode parent;
    private GraphNodeType type;
    private float weightPhysical;
    private float weightSupported;
    private boolean contains_product = false;

    public boolean contains_product() {
        return contains_product;
    }

    public void setContains_product(boolean contains_product) {
        if (this.type.equals(GraphNodeType.SIMPLE)) {
            this.contains_product = contains_product;
        }
    }

    private MyCluster cluster;

    public float getAmplify() {
        return amplify;
    }

    public GraphNode(float amplify, int id, Coordenates location, double heuristic, GraphNode parent, GraphNodeType type, float weightPhysical, float weightSupported, MyCluster cluster, int f, int g, List<Edge> neighbours) {
        this.amplify = amplify;
        this.id = id;
        this.location = (Coordenates) location.clone();
        this.type = type;
    }

    @Override
    protected Object clone() {
        return new GraphNode(this.amplify,
                this.id,
                this.location,
                this.heuristic,
                this.parent,
                this.type,
                this.weightPhysical,
                this.weightSupported,
                this.cluster,
                this.f,
                this.g,
                this.neighbours);
    }

    public void setAmplify(float amplify) {
        this.amplify = amplify;
    }

    public float getWeightSupported() {
        if (this.getType() == GraphNodeType.PRODUCT) {
            return weightSupported;
        } else {
            return -1;
        }
    }

    public void setWeightSupported(float weightSupported) {
        if (this.getType() == GraphNodeType.PRODUCT) {
            this.weightSupported = weightSupported;
        }
    }

    public float getWeightPhysical() {
        if (this.getType() == GraphNodeType.PRODUCT) {
            return weightPhysical;
        } else {
            return -1;
        }
    }

    public void setWeightPhysical(float weightPhysical) {
        if (this.getType() == GraphNodeType.PRODUCT) {
            this.weightPhysical = weightPhysical;
        }
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

    public Coordenates getLocationAmplified() {
        return location.amplified(amplify);
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
            //System.out.println("This edge has already been used for this node.");
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraphNode) {
            return this.getGraphNodeId() == ((GraphNode) obj).getGraphNodeId();
        } else {
            return false;
        }

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

    public GraphNode(int id, float x, float y, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = new Coordenates(x, y, 0);
        this.f = 0;
        this.heuristic = 0;
    }

    public GraphNode(int id, float x, float y, float z, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = new Coordenates(x, y, z);
        this.f = 0;
        this.heuristic = 0;
    }

    public GraphNode(int id, GraphNode node, GraphNodeType type) {
        this.id = id;
        this.type = type;
        this.location = node.getLocation();
        this.f = 0;
        this.heuristic = 0;
    }


    public void calculateHeuristic(GraphNode finalGraphNode) {
        try {
            int distance = Math.abs((int) (finalGraphNode.getLocation().getX() - this.getLocation().getX())) + Math.abs((int) (finalGraphNode.getLocation().getY() - this.getLocation().getY()));
            this.heuristic = distance;
        } catch (Exception e) {
            throw e;
        }
    }

    public float getDistance(GraphNode node) {
        float distance = Math.abs((int) (node.getLocation().getX() - this.getLocation().getX())) + Math.abs((int) (node.getLocation().getY() - this.getLocation().getY()));
        return distance;
    }

    public float getDistance(float x, float y) {
        float distance = Math.abs((int) (x - this.getLocation().getX())) + Math.abs((int) (y - this.getLocation().getY()));
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

    public List<GraphNode> getVerticalSimpleNode() {
        List<GraphNode> list = new ArrayList<>();
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getOtherEnd(this).getType() == GraphNodeType.SIMPLE && neighbours.get(i).getOtherEnd(this).getLocation().getX() == this.getLocation().getX()) {
                list.add(neighbours.get(i).getOtherEnd(this));
            }
        }
        return list;
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
        return this.type.toLetter() + "" + this.id + " " + this.getHeuristica() + " Cost " + this.getF() + " " + getType().toString() + " " + this.getLocation().toString() + "\n\t\t";
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

    public String getGraphNodeIdStr() {
        return this.type.toLetter() + "" + this.getGraphNodeId();
    }

    public void setCluster(MyCluster cluster) {
        this.cluster = cluster;
    }

    public MyCluster getCluster() {
        return cluster;
    }


    public String printName() {
        return this.getType().toLetter() + "" + this.getGraphNodeId();
    }

}
