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
    private HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodes;

    public void addTaskedAgent(GraphNode agent, List<GraphNode> path, List<Float> costs) {
        try {
            /*
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
                for (int j = 1; j < clone3.size(); j++) {
                    clone3.set(j, clone3.get(j) + weight);
                }
            }*/

            List<FitnessNode> fitnessNodes = new ArrayList<>();
            float sum = 0;
            for (int i = 0; i < path.size(); i++) {
                if (path.get(i).getGraphNodeId() != agent.getGraphNodeId()) {
                    if (path.size() != costs.size()) {
                        throw new IndexOutOfBoundsException("[ERROR] path/cost sizes diferent");
                    } else {
                        sum += costs.get(i);
                        FitnessNode fullNode = new FitnessNode(i, path.get(i), costs.get(i), i == 0 ? costs.get(i) : sum);
                        fitnessNodes.add(fullNode);
                    }
                }
            }
            taskedAgentsFullNodes.put(agent, fitnessNodes);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public FitnessResults() {
        path = new ArrayList<>();
        taskedAgentsFullNodes = new HashMap<>();
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
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : getTaskedAgentsFullNodes().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> agentPath = entry.getValue();
            str += "\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId();
            if (agentPath.isEmpty()) {
                str += "\n\tPath: ";
                for (int i = 0; i < agentPath.size(); i++) {
                    str += "[" + agentPath.get(i).getNode().getType().toLetter() + agentPath.get(i).getNode().getGraphNodeId() + "]";
                }
                str += "Size: " + agentPath.size() + "\n\tCost: ";
                for (int i = 0; i < agentPath.size(); i++) {
                    str += "[" + agentPath.get(i).getCost().toString() + "]";
                }
                str += "\n\tTime: ";
                for (int i = 0; i < agentPath.size(); i++) {
                    str += " " + agentPath.get(i).getTime().toString() + " ";
                }
            }
            else{
                str += "\t Idle";
            }
        }
        return str;
    }

    public HashMap<GraphNode, List<FitnessNode>> getTaskedAgentsFullNodes() {
        return taskedAgentsFullNodes;
    }

    public void setTaskedAgentsFullNodes(HashMap<GraphNode, List<FitnessNode>> taskedAgentsFullNodes) {
        this.taskedAgentsFullNodes = taskedAgentsFullNodes;
    }

    public class FitnessNode {
        private int id;
        private GraphNode node;
        private Float cost;

        public FitnessNode(int id, GraphNode node, Float cost, Float time) {
            this.id = id;
            this.node = node;
            this.cost = cost;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public GraphNode getNode() {
            return node;
        }

        public void setNode(GraphNode node) {
            this.node = node;
        }

        public Float getCost() {
            return cost;
        }

        public void setCost(Float cost) {
            this.cost = cost;
        }

        public Float getTime() {
            return time;
        }

        public void setTime(Float time) {
            this.time = time;
        }

        private Float time;
    }
}
