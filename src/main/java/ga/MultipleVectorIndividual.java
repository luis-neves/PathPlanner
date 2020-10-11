package ga;

import picking.Item;
import utils.Graphs.FitnessNode;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;

import java.util.*;

public abstract class MultipleVectorIndividual<P extends Problem, I extends MultipleVectorIndividual> extends Individual<P, I> {

    protected HashMap<GraphNode, List<GraphNode>> genome;

    public MultipleVectorIndividual(P problem, HashMap<GraphNode, List<GraphNode>> items) {
        super(problem);
        genome = new HashMap<>();
        for (Map.Entry<GraphNode, List<GraphNode>> entry : items.entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentPath = entry.getValue();
            List<GraphNode> path = new ArrayList<>();
            path.addAll(agentPath);
            Collections.shuffle(path, GeneticAlgorithm.random);
            genome.put(agent, path);
        }
    }

    public MultipleVectorIndividual(MultipleVectorIndividual<P, I> original) {
        super(original);
        genome = new HashMap<>();
        for (Map.Entry<GraphNode, List<GraphNode>> entry : original.genome.entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentPath = entry.getValue();
            List<GraphNode> path = new ArrayList<>();
            path.addAll(agentPath);
            genome.put(agent, path);
        }
    }

    @Override
    public int getNumGenes() {
        return genome.keySet().size();
    }

    public int getNumGenes(int idx) {
        int counter = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            if (counter == idx) {
                List<GraphNode> agentPath = entry.getValue();
                return agentPath.size();
            }
            counter++;
        }
        return -1;
    }

    @Override
    public Item getGene(GraphNode agent, int idx) {
        return new Item(genome.get(agent).get(idx));
    }


    private String printTaskedAgents() {
        try {
            String str = "";
            for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
                GraphNode agent = entry.getKey();
                List<FitnessNode> agentPath = entry.getValue();
                str += "\n\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId();
                if (!agentPath.isEmpty()) {
                    List<GraphNode> taskedAgentsOnly = results.getTaskedAgentsOnly().get(agent);
                    for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                        str += "\t[" + taskedAgentsOnly.get(i).getType().toLetter() + taskedAgentsOnly.get(i).getGraphNodeId() + "]";
                    }
                    str += " | Steps: " + agentPath.size();
                    if (GASingleton.getInstance().isSimulatingWeights()) {
                        str += "\n\t";
                        for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                            str += "\t[" + (int) taskedAgentsOnly.get(i).getWeightPhysical() + "]";
                        }
                        str += "\tWeight\n\t";
                        for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                            str += "\t[" + (int) taskedAgentsOnly.get(i).getWeightSupported() + "]";
                        }
                        str += "\tSupported Weight\n\t";
                        for (int i = 0; i < this.nodesSupport.get(agent).size(); i++) {
                            str += "\t[" + nodesSupport.get(agent).get(i).intValue() + "]";
                        }
                        str += "\t\tSupporting\n";
                    }

                    str += "\n\tPath: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getNode().getType().toLetter() + agentPath.get(i).getNode().getGraphNodeId() + "]";
                    }
                    str += "\n\tCost: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getCost().intValue() + "]";
                    }
                    str += "\n\tTime: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t " + agentPath.get(i).getTime().intValue() + " ";
                    }
                } else {
                    str += "\t Idle";
                }
            }
            str += "\nColisions: ";
            for (int i = 0; i < results.getColisions().size(); i++) {
                str += results.getColisions().get(i).print();
            }
            return str;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String printGenome() {
        try {
            String str = "";
            for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
                GraphNode agent = entry.getKey();
                List<FitnessNode> agentPath = entry.getValue();
                str += "\n\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId();
                if (!agentPath.isEmpty()) {
                    List<GraphNode> taskedAgentsOnly = results.getTaskedAgentsOnly().get(agent);
                    for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                        str += "\t[" + taskedAgentsOnly.get(i).getType().toLetter() + taskedAgentsOnly.get(i).getGraphNodeId() + "]";
                    }
                    str += " | Steps: " + agentPath.size();
                    if (GASingleton.getInstance().isSimulatingWeights()) {
                        str += "\n\t";
                        for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                            str += "\t[" + (int) taskedAgentsOnly.get(i).getWeightPhysical() + "]";
                        }
                        str += "\tWeight\n\t";
                        for (int i = 0; i < taskedAgentsOnly.size(); i++) {
                            str += "\t[" + (int) taskedAgentsOnly.get(i).getWeightSupported() + "]";
                        }
                        str += "\tSupported Weight\n\t";
                        for (int i = 0; i < this.nodesSupport.get(agent).size(); i++) {
                            str += "\t[" + nodesSupport.get(agent).get(i).intValue() + "]";
                        }
                        str += "\t\tSupporting\n";
                    }

                    str += "\n\tPath: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getNode().getType().toLetter() + agentPath.get(i).getNode().getGraphNodeId() + "]";
                    }
                    str += "\n\tCost: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t[" + agentPath.get(i).getCost().intValue() + "]";
                    }
                    str += "\n\tTime: ";
                    for (int i = 0; i < agentPath.size(); i++) {
                        str += "\t " + agentPath.get(i).getTime().intValue() + " ";
                    }
                } else {
                    str += "\t Idle";
                }
            }
            str += "\nColisions: ";
            for (int i = 0; i < results.getColisions().size(); i++) {
                str += results.getColisions().get(i).print();
            }
            return str;
        } catch (NullPointerException e) {
            //e.printStackTrace();
            return "";
        }
    }


    @Override
    public void swapGenes(MultipleVectorIndividual other, int index) {
        Map<GraphNode, List<GraphNode>> otherMap = other.genome;
        int seed = GASingleton.getInstance().getSeed();

        Random rand = new Random(seed);
        int indexToGet = rand.nextInt(genome.keySet().size() - 1);
        int currIndex = 0;
        GraphNode nodeToExchange = null;
        GraphNode myAgent = null;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            if (currIndex == indexToGet) {
                myAgent = entry.getKey();
                List<GraphNode> nodes = entry.getValue();
                nodeToExchange = nodes.get(index);
                break;
            }
            currIndex++;
        }

        GraphNode otherNodeToExchange = null;
        GraphNode otherAgent = null;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : otherMap.entrySet()) {
            List<GraphNode> nodes = entry.getValue();
            if (nodes.contains(nodeToExchange)) {
                otherAgent = entry.getKey();
                otherNodeToExchange = nodes.get(index);
                break;
            }
        }

        List<GraphNode> otherList = (List<GraphNode>) other.genome.get(otherAgent);
        List<GraphNode> myList = (List<GraphNode>) this.genome.get(myAgent);

        int my_other_index = myList.indexOf(otherNodeToExchange);
        int other_my_index = otherList.indexOf(nodeToExchange);

        myList.remove(my_other_index);
        myList.add(nodeToExchange);
        otherList.remove(other_my_index);
        otherList.add(otherNodeToExchange);

        myList.remove(index);
        myList.add(index, otherNodeToExchange);
        otherList.remove(index);
        otherList.add(index, nodeToExchange);
    }
}
