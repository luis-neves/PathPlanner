package utils.Graphs;

import ga.KMeans.MyCluster;
import utils.warehouse.Coordenates;
import weka.core.Attribute;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Graph {
    private List<GraphNode> graphNodes;
    private List<Edge> edges;


    public Graph(List<Edge> edges, List<GraphNode> graphNodes, int numberOfEdges, int numberOfgraphNodes) {
        this.edges = edges;
        this.graphNodes = graphNodes;
        this.numberOfEdges = numberOfEdges;
        this.numberOfgraphNodes = numberOfgraphNodes;
    }

    public Graph() {
        graphNodes = new ArrayList<GraphNode>();
        edges = new ArrayList<Edge>();
    }

    public void setNumberOfgraphNodes(int numberOfgraphNodes) {
        this.numberOfgraphNodes = numberOfgraphNodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    private int numberOfgraphNodes = 0;
    private int numberOfEdges = 0;

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public boolean checkForAvailability() { // will be used in Main.java
        return this.numberOfgraphNodes > 1;
    }

    public void createGraphNode(GraphNode GraphNode) {
        this.graphNodes.add(GraphNode);
        this.numberOfgraphNodes++; // a GraphNode has been added
    }

    public void createEdge(Edge edge) {
        edge.getStart().addNeighbour(edge);
        edge.getEnd().addNeighbour(edge);
        if (edge.getStart().getLocation().getX() == edge.getEnd().getLocation().getX()) {
            edge.setLocation(new Coordenates(Math.abs(edge.getStart().getLocation().getX() - edge.getEnd().getLocation().getX()), 0, 0));
        } else if (edge.getStart().getLocation().getY() == edge.getEnd().getLocation().getY()) {
            edge.setLocation(new Coordenates(0,Math.abs(edge.getStart().getLocation().getY() - edge.getEnd().getLocation().getY()), 0));
        }
        else {
            edge.setLocation(new Coordenates(Math.abs(edge.getStart().getLocation().getX() - edge.getEnd().getLocation().getX()), Math.abs(edge.getStart().getLocation().getY() - edge.getEnd().getLocation().getY()), Math.abs(edge.getStart().getLocation().getZ() - edge.getEnd().getLocation().getZ())));

        }
        this.edges.add(edge);
        this.numberOfEdges++; // a GraphNode has been added
    }

    public int getNumberOfgraphNodes() {
        return this.numberOfgraphNodes;
    }

    public List<GraphNode> getGraphNodes() {
        return graphNodes;
    }

    public GraphNode getTrueNode(GraphNode graphNode) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getGraphNodeId() == graphNode.getGraphNodeId()) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }
    public void amplify(float value){
        for(GraphNode node : graphNodes){
            node.setAmplify(value);
        }
    }

    @Override
    public Graph clone() {
        Graph graph = new Graph();
        List<GraphNode> graphNodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        graphNodes.addAll(getGraphNodes());
        edges.addAll(getEdges());
        graph.setEdges(edges);
        graph.setgraphNodes(graphNodes);
        return graph;
    }

    public void setgraphNodes(List<GraphNode> graphNodes) {
        this.graphNodes = graphNodes;
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

    public void removeNode(GraphNode node) {
        if (node != null) {
            graphNodes.remove(node);
            List<Edge> toRemove = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge.getEnd().equals(node) || edge.getStart().equals(node)) {
                    toRemove.add(edge);
                }
            }
            edges.removeAll(toRemove);
            numberOfgraphNodes--;
        }
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
        if (products.isEmpty()) {
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
        if (graphNodes != null) {
            return this.graphNodes.size();
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

    public GraphNode findClosestNode(float x, float y, float sensibility) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            GraphNode node = getGraphNodes().get(i);
            if (x - sensibility < node.getLocation().getX() && node.getLocation().getX() < x + sensibility) {
                if (y - sensibility < node.getLocation().getY() && node.getLocation().getY() < y + sensibility) {
                    return node;
                }
            }

        }
        return null;
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
                mostRight = getGraphNodes().get(i);
            }
        }
        return Math.round(mostRight.getLocation().getX());
    }

    public int getDimensionY() {
        GraphNode mostBottom = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (mostBottom == null || mostBottom.getLocation().getY() < getGraphNodes().get(i).getLocation().getY()) {
                mostBottom = getGraphNodes().get(i);
            }
        }
        return Math.round(mostBottom.getLocation().getY());
    }

    public void createGraphNode(int x, int y, GraphNodeType type) {
        this.graphNodes.add(new GraphNode(this.numberOfgraphNodes, (float) x, (float) y, type));
        this.numberOfgraphNodes++;
    }

    public GraphNode getLastNode() {
        if (graphNodes.size() > 0) {
            return graphNodes.get(graphNodes.size() - 1);
        }
        return null;

    }


    public void makeNeighbors(GraphNode start_node, GraphNode end_node, boolean product_line) {
        try {
            Edge e = findEdge(start_node, end_node);
            if (e == null){
                e = new Edge(start_node, end_node, start_node.getDistance(end_node), edges.size(), product_line);
                e.setEnd(start_node);
                e.setStart(end_node);
                createEdge(e);
            }
            else{
                e.setProduct_line(product_line);
            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    private Edge findEdge(GraphNode start_node, GraphNode end_node) {
        for (Edge edge : edges){
            if(edge.getEnd() == end_node && edge.getStart() == start_node){
                return edge;
            }
            if(edge.getEnd() == start_node && edge.getStart() == end_node){
                return edge;
            }
        }
        return null;
    }

    public void clear() {
        graphNodes.clear();
        edges.clear();
    }

    public void deAmplify(float amplify_x) {
        for(GraphNode node : graphNodes){
            node.getLocation().setX(node.getLocation().getX()/amplify_x);
            node.getLocation().setY(node.getLocation().getY()/amplify_x);
        }
    }
}
