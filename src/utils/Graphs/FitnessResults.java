package utils.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitnessResults {
    private float fitness;
    private List<GraphNode> path;
    private int numCollisions;

    public int getNumCollisions() {
        return numCollisions;
    }

    public void setNumCollisions(int numCollisions) {
        this.numCollisions = numCollisions;
    }

    public float getCollisionPenalty() {
        return collisionPenalty;
    }

    public void setCollisionPenalty(float collisionPenalty) {
        this.collisionPenalty = collisionPenalty;
    }

    private float collisionPenalty;
    private HashMap<GraphNode, List<GraphNode>> taskedAgents;
    private HashMap<GraphNode, List<Float>> taskedAgentsCosts;
    private HashMap<GraphNode, List<Float>> taskedAgentsCostsTime;

    public void setTaskedAgentsCostsTime(HashMap<GraphNode, List<Float>> taskedAgentsCostsTime) {
        this.taskedAgentsCostsTime = taskedAgentsCostsTime;
    }

    public HashMap<GraphNode, List<Float>> getTaskedAgentsCostsTime() {
        return taskedAgentsCostsTime;
    }

    public HashMap<GraphNode, List<Float>> getTaskedAgentsCosts() {
        return taskedAgentsCosts;
    }

    public void setTaskedAgentsCosts(HashMap<GraphNode, List<Float>> taskedAgentsCosts) {
        this.taskedAgentsCosts = taskedAgentsCosts;
    }

    public HashMap<GraphNode, List<GraphNode>> getTaskedAgents() {
        return taskedAgents;
    }

    public void addTaskedAgent(GraphNode agent, List<GraphNode> path, List<Float> costs) {
        List<GraphNode> clone = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getGraphNodeId() != agent.getGraphNodeId()) {
                if (i < path.size() - 1 && path.get(i).getGraphNodeId() != path.get(i + 1).getGraphNodeId()) {
                    clone.add(path.get(i));
                }
            }
        }
        List<Float> clone2 = new ArrayList<>();
        List<Float> clone3 = new ArrayList<>();
        float sum = 0;
        for (int i = 0; i < costs.size(); i++) {
            clone2.add(costs.get(i));
            sum += costs.get(i);
            clone3.add(sum);
        }
        if (!path.isEmpty()) {
            clone.add(path.get(path.size() - 1));
        }
        if (clone.size() != clone2.size()) {
            float weight = (float) agent.getNodeWeight(path.get(0));
            clone2.add(0, weight);
            clone3.add(0, weight);
            for (int j = 1; j < clone3.size(); j++){
                clone3.set(j, clone3.get(j) + weight);
            }
        }
        taskedAgents.put(agent, clone);
        taskedAgentsCosts.put(agent, clone2);
        taskedAgentsCostsTime.put(agent, clone3);
    }

    public void removeTaskedAgent(GraphNode agent) {
        taskedAgents.remove(agent);
    }

    public List<GraphNode> getAgentPath(GraphNode agent) {
        return taskedAgents.get(agent);
    }

    public void setTaskedAgents(HashMap<GraphNode, List<GraphNode>> taskedAgents) {
        this.taskedAgents = taskedAgents;
    }

    public FitnessResults() {
        path = new ArrayList<>();
        taskedAgents = new HashMap<>();
        taskedAgentsCosts = new HashMap<>();
        taskedAgentsCostsTime = new HashMap<>();
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public List<GraphNode> getPath() {
        return path;
    }

    public void setPath(List<GraphNode> path) {
        this.path = path;
    }

    public FitnessResults(float fitness, List<GraphNode> path) {
        this.fitness = fitness;
        this.path = path;
    }

    public String printTaskedAgents() {
        String str = "";
        for (Map.Entry<GraphNode, List<GraphNode>> entry : getTaskedAgents().entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentPath = entry.getValue();
            List<Float> costs = getTaskedAgentsCosts().get(agent);
            List<Float> costsTime = getTaskedAgentsCostsTime().get(agent);
            str += "\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId() + "\n\t";

            for (int i = 0; i < costs.size(); i++) {
                str += "["+costs.get(i).toString() + "]";
            }
            str+= "Size: "+ costs.size() + "\n\t";
            for (int i = 0; i < costsTime.size(); i++) {
                str += " " + costsTime.get(i).toString()+ " ";
            }
            str+= "\n\t";
            for (int i = 0; i < agentPath.size(); i++) {
                str += "[" + agentPath.get(i).getType().toLetter() + agentPath.get(i).getGraphNodeId() + "]";
            }
            str+= "Size: "+ agentPath.size();

            if (agentPath.isEmpty()) {
                str += "Empty Path";
            }
        }
        return str;
    }
}
