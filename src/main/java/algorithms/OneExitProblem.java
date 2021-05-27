package algorithms;

import arwdatastruct.Agent;
import orderpicking.GNode;
import orderpicking.Pick;
import pathfinder.Graph;

import java.util.ArrayList;
import java.util.List;

public class OneExitProblem extends Problem{
    private Graph<GNode> graph;
    private List<Pick> picks;
    private Agent agent;
    private String exit;
    private double massCenterX;
    private double massCenterY;

    public OneExitProblem(Graph<GNode> graph) {
        this.graph = graph;
        this.picks = new ArrayList<>();
    }

    public OneExitProblem(Graph<GNode> graph, List<Pick> picks, Agent agent, String exit) {
        this.graph = graph;
        this.picks = picks;
        this.agent = agent;
        this.exit = exit;
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

    public void addPick(Pick pick) {
        this.picks.add(pick);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getExit() {
        return exit;
    }

    public void setExit(String exit) {
        this.exit = exit;
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
