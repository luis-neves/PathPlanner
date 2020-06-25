package utils.Graphs;

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
        if (edge.getStart().getLocation().getX() == edge.getEnd().getLocation().getX()) {
            edge.setLocation(new Coordenates(edge.getStart().getLocation().getX(), 0, 0));
        } else if (edge.getStart().getLocation().getY() == edge.getEnd().getLocation().getY()) {
            edge.setLocation(new Coordenates(0, edge.getStart().getLocation().getY(), 0));
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
}
