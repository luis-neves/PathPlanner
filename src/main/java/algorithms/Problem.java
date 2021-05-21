package algorithms;

import arwdatastruct.Agent;
import orderpicking.GNode;
import orderpicking.Pick;
import pathfinder.Graph;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    private Graph<GNode> graph;
    private List<Pick> picks;
    private List<Agent> agents;
    private double massCenterX;
    private double massCenterY;

    public Problem(Graph<GNode> graph) {
        this.graph = graph;
        this.picks = new ArrayList<>();
        this.agents = new ArrayList<>();
    }

    public Problem(Graph<GNode> graph, List<Pick> picks, List<Agent> agents) {
        this.graph = graph;
        this.picks = picks;
        this.agents = agents;
    }

    public void computeMassCenter(){
        massCenterX = massCenterY = 0;
        for(Pick pick : picks){
            massCenterX += pick.getNode().getX();
            massCenterY += pick.getNode().getY();
        }
        massCenterX /= picks.size();
        massCenterY /= picks.size();
    }


    public Graph<GNode> getGraph() {
        return graph;
    }

    public void setGraph(Graph<GNode> graph) {
        this.graph = graph;
    }

    public List<Pick> getPicks() {
        return picks;
    }

    public void setPicks(List<Pick> picks) {
        this.picks = picks;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    public void addAgent(Agent agent){
        agents.add(agent);
    }

    public double getMassCenterX() {
        return massCenterX;
    }

    public void setMassCenterX(double massCenterX) {
        this.massCenterX = massCenterX;
    }

    public double getMassCenterY() {
        return massCenterY;
    }

    public void setMassCenterY(double massCenterY) {
        this.massCenterY = massCenterY;
    }
}
