package picking;

import ga.GASingleton;
import ga.MultipleVectorIndividual;
import ga.VectorIndividual;
import gui.SimulationPanel;
import utils.Graphs.EnvironmentNodeGraph;
import utils.Graphs.FitnessResults;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;
import utils.Maths;

import java.util.*;

public class HybridPickingIndividual extends MultipleVectorIndividual<HybridClusterPicking, HybridPickingIndividual> {

    public HybridPickingIndividual(HybridClusterPicking problem, HashMap<GraphNode, List<GraphNode>> taskMap) {
        super(problem, taskMap);
    }

    public HybridPickingIndividual(HybridPickingIndividual original) {
        super(original);
    }

    @Override
    public double computeFitness() {
        if (GASingleton.getInstance().isNodeProblem()) {
            FitnessResults results = SimulationPanel.environmentNodeGraph.calculatePath(genome);
            this.results = results;
            fitness = results.getFitness() * GASingleton.getInstance().getTimeWeight();

            fitness += ((results.getCollisionPenalty() * results.getNumCollisions()) * GASingleton.getInstance().getColisionWeight()) * GASingleton.getInstance().getColisionWeight();
            int agentsPick = 0;
            avgPicksPerAgent = 0;

            if (GASingleton.getInstance().isSimulatingWeights()) {
                float weightsPenalty = 0;
                this.nodesSupport = new HashMap<>();
                for (Map.Entry<GraphNode, List<GraphNode>> entry : results.getTaskedAgentsOnly().entrySet()) {
                    GraphNode agent = entry.getKey();
                    List<GraphNode> nodes = entry.getValue();
                    List<Float> supports = new ArrayList<>();
                    for (int i = 0; i < nodes.size() - 1; i++) {
                        float value = nodes.get(i).getWeightSupported();
                        float sum = 0;
                        for (int j = i + 1; j < nodes.size(); j++) {
                            sum += nodes.get(j).getWeightPhysical();
                        }
                        supports.add(sum);
                        if (value < sum) {
                            weightsPenalty += (sum - value);
                        }
                    }
                    this.nodesSupport.put(agent, supports);
                }
                this.results.setWeightsPenalty(weightsPenalty);
                fitness += (weightsPenalty * GASingleton.getInstance().getWeightsPenaltyWeight()) * GASingleton.getInstance().getWeightsPenaltyWeight();
            }

            //System.out.println(results.printTaskedAgents());
            //System.out.println(printGenome() + " - " + fitness);

        } else {
            fitness = 0;
            collisions = 0;
            avgPicksPerAgent = 0;
            List<Double> times = GASingleton.getInstance().setFinalItemSet(Arrays.asList(getGenome(-1)), false);
            int highest = 0;
            if (times != null) {
                for (Double value : times) {
                    if (times.indexOf(value) == times.size() - 1) {
                        avgPicksPerAgent = value;
                    } else if (times.indexOf(value) == times.size() - 2) {
                        collisions = value.intValue();
                    } else {
                        if (highest < value) {
                            highest = value.intValue();
                        }
                    }
                }
                fitness = highest;
            }
        }
        return fitness;
    }

    public double pickDistributionStdDev() {
        double[] picks = new double[results.getTaskedAgentsOnly().keySet().size()];
        int i = 0;
        int totalPicks = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : results.getTaskedAgentsOnly().entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> nodes = entry.getValue();
            picks[i++] = nodes.size();
            totalPicks += nodes.size();
        }
        avgPicksPerAgent = (double) totalPicks / results.getTaskedAgentsOnly().keySet().size();
        return Maths.standardDeviation(picks, avgPicksPerAgent);
    }

    @Override
    public void swapGenes(HybridPickingIndividual other, int index) {

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nfitness: " + fitness);
        sb.append("\nfitness breakdown: " + printFitnessBreakdown());
        sb.append("\nItems: ");
        sb.append(printGenome());
        //sb.append("\nPicks Per Agent:" + picksPerAgent);

        return sb.toString();
    }

    private String printFitnessBreakdown() {
        String str = "";
        if (results != null)
            str += results.getFitness() + "*" + GASingleton.getInstance().getTimeWeight() + " + (" + (results.getCollisionPenalty() * results.getNumCollisions()) + (GASingleton.getInstance().isSimulatingWeights() ? " * " + GASingleton.getInstance().getColisionWeight() + (") + (" + (results.getWeightsPenalty()) + " * " + GASingleton.getInstance().getWeightsPenaltyWeight() + ")") : "*" + GASingleton.getInstance().getColisionWeight() + ")");
        return str;
    }

    /**
     * @param i
     * @return 1 if this object is BETTER than i, -1 if it is WORST than I and 0, otherwise.
     */
    @Override
    public int compareTo(HybridPickingIndividual i) {
        return this.fitness == i.getFitness() ? 0 : this.fitness < i.getFitness() ? 1 : -1;
    }

    @Override
    public HybridPickingIndividual clone() {
        return new HybridPickingIndividual(this);
    }

    @Override
    public Item[] getGenome(GraphNode agent) {
        List<GraphNode> genome = this.genome.get(agent);
        Item[] items = new Item[genome.size()];
        for (int i = 0; i < genome.size(); i++) {
            items[i] = new Item(genome.get(i));
        }
        return items;
    }

    @Override
    public Item[] getGenome(int indexToGet) {
        int index = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            if (index == indexToGet) {
                GraphNode agent = entry.getKey();
                List<GraphNode> nodes = entry.getValue();
                Item[] items = new Item[nodes.size()];
                for (int i = 0; i < nodes.size(); i++) {
                    GraphNode node = nodes.get(i);
                    items[i] = new Item(node.getType().toLetter() + " " + node.getGraphNodeId(), node);
                }
                return items;
            }
            index++;
        }
        return null;
    }

    @Override
    public void replaceFromChild(GraphNode agent, List<Item> genome) {
        List<GraphNode> items = this.genome.get(agent);
        items.clear();
        for (Item item : genome) {
            items.add(item.node);
        }
    }

    @Override
    public void setGene(Integer agentIdx, Integer idx, Item value) {
        int index = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            if (index == agentIdx) {
                List<GraphNode> list = genome.get(entry.getKey());
                list.remove(value.node);
                list.add(idx, value.node);
                break;
            }
            index++;
        }
    }

    @Override
    public GraphNode getAgent(int agentIdx) {
        int index = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            if (index == agentIdx) {
                GraphNode agent = entry.getKey();
                return agent;
            }
            index++;
        }
        return null;
    }

    public void replaceFromChild(HashMap<GraphNode, List<GraphNode>> genome) {
        this.genome = new HashMap<>();
        for (Map.Entry<GraphNode, List<GraphNode>> entry : genome.entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentPath = entry.getValue();
            this.genome.put(agent, agentPath);
        }
    }
}
