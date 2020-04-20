package utils.warehouse;

import armazem.Agent;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class Colision {
    private List<GraphNode> agents;
    private List<GraphNode> nodes;
    private List<Float> times;
    private String type = "";

    public Colision() {
        this.agents = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.times = new ArrayList<>();
    }

    public void addAgent(GraphNode a) {
        this.agents.add(a);
    }

    public void addNode(GraphNode n) {
        this.nodes.add(n);
    }

    public void addTime(Float t) {
        this.times.add(t);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GraphNode> getAgents() {
        return agents;
    }

    public void setAgents(List<GraphNode> agents) {
        this.agents = agents;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<Float> getTimes() {
        return times;
    }

    public void setTimes(List<Float> times) {
        this.times = times;
    }

    public String print() {
        String str = "\n--------Node-----------------------------------\n";
        for (int i = 0; i < nodes.size(); i += 2) {
            if (nodes.size() > 1) {
                str += "\t" + nodes.get(i).getType().toLetter() + (nodes.get(i).getGraphNodeId() + "") + "->" + nodes.get(i + 1).getType().toLetter() + (nodes.get(i + 1).getGraphNodeId() + "") + "\t";
            } else {
                str += "\t" + nodes.get(i).getType().toLetter() + (nodes.get(i).getGraphNodeId() + "");
            }
        }
        str += " " + getType() + "\n";
        for (int i = 0; i < agents.size(); i++) {
            str += "\tA" + agents.get(i).getGraphNodeId() + "\t\t";
        }
        str += "\nCosts";
        for (int i = 0; i < times.size(); i += 2) {
            str += "\t[" + times.get(i) + " , " + times.get(i + 1) + "]\t";
        }

        return str;
    }
}
