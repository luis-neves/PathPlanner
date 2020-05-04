package utils.Graphs;

import utils.warehouse.Colision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitnessResults {
    private float fitness;
    private float time;

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    private List<GraphNode> path;
    private List<Colision> colisions;
    private int numCollisions;
    private float weightsPenalty;

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
    private HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodes;
    private HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodesNoPackages;
    private HashMap<GraphNode, List<GraphNode>> taskedAgentsOnly;

    public HashMap<GraphNode, List<FitnessNode>> getTaskedAgentsFullNodesNoPackages() {
        return taskedAgentsFullNodesNoPackages;
    }

    public void setTaskedAgentsFullNodesNoPackages(HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodesNoPackages) {
        this.taskedAgentsFullNodesNoPackages = taskedAgentsFullNodesNoPackages;
    }

    public HashMap<GraphNode, List<GraphNode>> getTaskedAgentsOnly() {
        return taskedAgentsOnly;
    }

    public void setTaskedAgentsOnly(HashMap<GraphNode, List<GraphNode>> taskedAgentsOnly) {
        this.taskedAgentsOnly = taskedAgentsOnly;
    }

    public void addTaskedAgent(GraphNode agent, List<GraphNode> path, List<Float> costs) {
        try {
            List<FitnessNode> fitnessNodes = new ArrayList<>();
            List<FitnessNode> fitnessNodesNoPackages = new ArrayList<>();
            float sum = 0;
            for (int i = 0; i < path.size(); i++) {
                if (path.get(i).getGraphNodeId() != agent.getGraphNodeId()) {
                    if (path.size() != costs.size()) {
                        throw new IndexOutOfBoundsException("[ERROR] path/cost sizes diferent");
                    } else {
                        sum += costs.get(i);
                        FitnessNode fullNode = new FitnessNode(i, path.get(i), costs.get(i), i == 0 ? costs.get(i) : sum);
                        fitnessNodes.add(fullNode);
                        if (fullNode.getNode().getType() != GraphNodeType.PRODUCT) {
                            fitnessNodesNoPackages.add(fullNode);
                        }
                    }
                }
            }
            taskedAgentsFullNodes.put(agent, fitnessNodes);
            taskedAgentsFullNodesNoPackages.put(agent, fitnessNodesNoPackages);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public FitnessResults() {
        path = new ArrayList<>();
        taskedAgentsFullNodes = new HashMap<>();
        taskedAgentsFullNodesNoPackages = new HashMap<>();
        colisions = new ArrayList<>();
        taskedAgentsOnly = new HashMap<>();
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
        try {
            String str = "";
            for (Map.Entry<GraphNode, List<FitnessNode>> entry : getTaskedAgentsFullNodes().entrySet()) {
                GraphNode agent = entry.getKey();
                List<FitnessNode> agentPath = entry.getValue();
                str += "\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId();
                if (!agentPath.isEmpty()) {
                    List<GraphNode> taskedAgentsOnly = getTaskedAgentsOnly().get(agent);
                    for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                        str += "\t[" + taskedAgentsOnly.get(i).getType().toLetter() + taskedAgentsOnly.get(i).getGraphNodeId() + "]";
                    }
                    str += " | Steps: " + agentPath.size();
                    str += "\n\tPath: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getNode().getType().toLetter() + agentPath.get(i).getNode().getGraphNodeId() + "]";
                    }
                    str += "\n\tCost: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getCost().toString() + "]";
                    }
                    str += "\n\tTime: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t " + agentPath.get(i).getTime().toString() + " ";
                    }
                } else {
                    str += "\t Idle";
                }
            }
            str += "\n Colisions: ";
            for (int i = 0; i < getColisions().size(); i++) {
                str += getColisions().get(i).print();
            }
            return str;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    public HashMap<GraphNode, List<FitnessNode>> getTaskedAgentsFullNodes() {
        return taskedAgentsFullNodes;
    }

    public void setTaskedAgentsFullNodes(HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodes) {
        this.taskedAgentsFullNodes = taskedAgentsFullNodes;
    }

    public void setColisions(List<Colision> colisions) {
        this.colisions = colisions;
    }

    public List<Colision> getColisions() {
        return colisions;
    }

    public void addTaskedAgentOnly(GraphNode agent, List<GraphNode> taskedAgentOnly) {
        List<GraphNode> clone = new ArrayList<>(taskedAgentOnly);
        this.taskedAgentsOnly.put(agent, clone);
    }

    public void clearNodeListFromPackages() {
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : getTaskedAgentsFullNodesNoPackages().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> agentPath = entry.getValue();
            List<FitnessNode> toRemove = new ArrayList<>();
            for (FitnessNode node : agentPath) {
                if (node.getNode().getType() == GraphNodeType.PRODUCT) {
                    toRemove.add(node);
                }
            }
            agentPath.removeAll(toRemove);
        }
    }

    public void setWeightsPenalty(float weightsPenalty) {
        this.weightsPenalty = weightsPenalty;
    }

    public float getWeightsPenalty() {
        return weightsPenalty;
    }
}
