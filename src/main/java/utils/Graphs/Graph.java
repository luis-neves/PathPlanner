package utils.Graphs;

import ga.KMeans.MyCluster;
import utils.warehouse.Coordenates;
import weka.core.Attribute;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Graph {
    private List<GraphNode> GraphNodes;
    private List<Edge> edges;

    public Graph(List<Edge> edges, List<GraphNode> graphNodes, int numberOfEdges, int numberOfGraphNodes) {
        this.edges = edges;
        this.GraphNodes = graphNodes;
        this.numberOfEdges = numberOfEdges;
        this.numberOfGraphNodes = numberOfGraphNodes;
    }

    public Graph() {
        GraphNodes = new ArrayList<GraphNode>();
        edges = new ArrayList<Edge>();
    }

    public void setNumberOfGraphNodes(int numberOfGraphNodes) {
        this.numberOfGraphNodes = numberOfGraphNodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    private int numberOfGraphNodes = 0;
    private int numberOfEdges = 0;

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public boolean checkForAvailability() { // will be used in Main.java
        return this.numberOfGraphNodes > 1;
    }

    public void createGraphNode(GraphNode GraphNode) {
        this.GraphNodes.add(GraphNode);
        this.numberOfGraphNodes++; // a GraphNode has been added
    }

    public void createEdge(Edge edge) {
        edge.getStart().addNeighbour(edge);
        edge.getEnd().addNeighbour(edge);
        if (edge.getStart().getDrawLocation() == null) {
            if (edge.getStart().getLocation().getX() == edge.getEnd().getLocation().getX()) {
                edge.setLocation(new Coordenates(edge.getStart().getLocation().getX(), 0, 0));
            } else if (edge.getStart().getLocation().getY() == edge.getEnd().getLocation().getY()) {
                edge.setLocation(new Coordenates(0, edge.getStart().getLocation().getY(), 0));
            }
        } else {
            if (edge.getStart().getDrawLocation().getX() == edge.getEnd().getDrawLocation().getX()) {
                edge.setLocation(new Coordenates(edge.getStart().getDrawLocation().getX(), 0, 0));
            } else if (edge.getStart().getDrawLocation().getY() == edge.getEnd().getDrawLocation().getY()) {
                edge.setLocation(new Coordenates(0, edge.getStart().getDrawLocation().getY(), 0));
            }
        }

        this.edges.add(edge);
        this.numberOfEdges++; // a GraphNode has been added
    }

    public int getNumberOfGraphNodes() {
        return this.numberOfGraphNodes;
    }

    public List<GraphNode> getGraphNodes() {
        return GraphNodes;
    }

    public GraphNode getTrueNode(GraphNode graphNode) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getGraphNodeId() == graphNode.getGraphNodeId()) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }

    @Override
    public Graph clone() {
        Graph graph = new Graph();
        List<GraphNode> GraphNodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        GraphNodes.addAll(getGraphNodes());
        edges.addAll(getEdges());
        graph.setEdges(edges);
        graph.setGraphNodes(GraphNodes);
        return graph;
    }

    private void setGraphNodes(List<GraphNode> graphNodes) {
        this.GraphNodes = graphNodes;
    }

    public boolean containsProblem() {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                return true;
            }
        }
        return false;
    }

    public void unfixAgentNeighbours() {
        for (GraphNode node : getGraphNodes()) {
            if (node.getType() == GraphNodeType.AGENT) {
                node.removeNeighbours();
                edges.removeIf(edge -> edge.getStart() == node || edge.getEnd() == node);
            }
        }
    }

    public void removeNode(GraphNode currentObjective) {
        GraphNodes.remove(currentObjective);
    }

    public void makeDelivering(GraphNode node) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getGraphNodeId() == node.getGraphNodeId()) {
                getGraphNodes().get(i).setType(GraphNodeType.DELIVERING);
            }
        }
    }

    public GraphNode findNode(int node_id) {
        for (int i = 0; i < this.getGraphNodes().size(); i++) {
            if (this.getGraphNodes().get(i).getGraphNodeId() == node_id) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }

    public int getProductsNum() {
        int products = 0;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT) {
                products++;
            }
        }
        return products;
    }

    public int getAgentsNum() {
        int agents = 0;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                agents++;
            }
        }
        return agents;
    }

    public List<GraphNode> getProducts() {
        List<GraphNode> products = new ArrayList<>();
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT) {
                products.add(getGraphNodes().get(i));
            }
        }
        return products;
    }

    public List<GraphNode> getProductsByCluster(MyCluster cluster) {
        List<GraphNode> products = new ArrayList<>();
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT && getGraphNodes().get(i).getCluster().equals(cluster)) {
                products.add(getGraphNodes().get(i));
            }
        }
        if(products.isEmpty()){
            System.out.println("Empty products");
        }
        return products;
    }

    public List<GraphNode> getAgents() {
        List<GraphNode> agents = new ArrayList<>();
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                agents.add(getGraphNodes().get(i));
            }
        }
        return agents;
    }

    public GraphNode getExit() {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.EXIT) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }

    public int getNumNodes() {
        if (GraphNodes != null) {
            return this.GraphNodes.size();
        } else {
            return 0;
        }
    }

    public GraphNode findClosestNode(float x, float y) {
        GraphNode closest = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            GraphNode node = getGraphNodes().get(i);
            if (closest == null || node.getDistance(x, y) <= closest.getDistance(x, y)) {
                closest = node;
            }
        }
        return closest;
    }

    public void bridge(GraphNode node1, GraphNode node2) {
        Edge edge = new Edge(node1, node2, node1.getDistance(node2), this.getEdges().size());
        this.getTrueNode(node1).addNeighbour(edge);
        this.getTrueNode(node2).addNeighbour(edge);
        this.createEdge(edge);
    }

    public void flipHorizontal(float v) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            GraphNode n = this.getGraphNodes().get(i);
            n.setLocation(new Coordenates(v - n.getLocation().getX(), n.getLocation().getY(), 0));
        }
    }

    public int getDimensionX() {
        GraphNode mostRight = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (mostRight == null || mostRight.getLocation().getX() < getGraphNodes().get(i).getLocation().getX()) {
                mostRight =  getGraphNodes().get(i);
            }
        }
        return Math.round(mostRight.getLocation().getX());
    }

    public int getDimensionY() {
        GraphNode mostBottom = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (mostBottom == null || mostBottom.getLocation().getY() < getGraphNodes().get(i).getLocation().getY()) {
                mostBottom =  getGraphNodes().get(i);
            }
        }
        return Math.round(mostBottom.getLocation().getY());
    }
}
