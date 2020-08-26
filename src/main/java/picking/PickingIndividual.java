package picking;

import ga.GASingleton;
import ga.VectorIndividual;
import gui.SimulationPanel;
import utils.Graphs.*;
import utils.Maths;

import java.util.*;

public class PickingIndividual extends VectorIndividual<Picking, PickingIndividual> {

    public PickingIndividual(Picking problem, List<Item> items) {
        super(problem, items);
    }

    public PickingIndividual(PickingIndividual original) {
        super(original);
    }

    @Override
    public double computeFitness() {
        if (GASingleton.getInstance().isNodeProblem()) {
            GraphNode responsibleAgent = null;
            FitnessResults results = new FitnessResults();
            if (GASingleton.getInstance().getTaskMap() == null) {
                responsibleAgent = GASingleton.getInstance().getLastAgent();
                results = SimulationPanel.environmentNodeGraph.calculatePaths(getGenome(-1), responsibleAgent);
            } else {
                responsibleAgent = GASingleton.getInstance().getResponsibleAgentFromArray(getGenome(-1)[0].node);
                EnvironmentNodeGraph env = GASingleton.getInstance().getRespectiveEnvironment(getGenome(-1)[0].node);
                results = env.calculatePaths(getGenome(-1), responsibleAgent);
            }
            this.results = results;
            fitness = results.getFitness() * GASingleton.getInstance().getTimeWeight();
            fitness += ((results.getCollisionPenalty() * results.getNumCollisions()) * GASingleton.getInstance().getColisionWeight()) * GASingleton.getInstance().getColisionWeight();
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
    public void swapGenes(PickingIndividual other, int index) {

    }


    @Override
    public void swapGenes(VectorIndividual other, int index) {
        int auxI = 0;
        for (int i = 0; i < genome.length; i++) {
            if (genome[i].name.equals(other.getGenome(-1)[index].name)) {
                auxI = i;
            }
        }
        Item aux = genome[index];
        Item replace = genome[auxI];
        genome[index] = other.getGenome(-1)[index];
        genome[auxI] = aux;
        for (int i = 0; i < other.getGenome(-1).length; i++) {
            if (other.getGenome(-1)[i].name.equals(aux.name)) {
                other.setGene(-1, i, replace);
            }
        }
        other.setGene(-1, index, aux);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nfitness: " + fitness);
        sb.append("\nfitness breakdown: " + printFitnessBreakdown());
        //sb.append("\nItems: ");
        sb.append(printGenome());
        //sb.append("\nPicks Per Agent:" + picksPerAgent);

        return sb.toString();
    }

    private String printFitnessBreakdown() {
        String str = "";
        str += results.getFitness() + "*" + GASingleton.getInstance().getTimeWeight() + " + (" + (results.getCollisionPenalty() * results.getNumCollisions()) + (GASingleton.getInstance().isSimulatingWeights() ? " * " + GASingleton.getInstance().getColisionWeight() + (") + (" + (results.getWeightsPenalty()) + " * " + GASingleton.getInstance().getWeightsPenaltyWeight() + ")") : "*" + GASingleton.getInstance().getColisionWeight() + ")");
        return str;
    }

    /**
     * @param i
     * @return 1 if this object is BETTER than i, -1 if it is WORST than I and 0, otherwise.
     */
    @Override
    public int compareTo(PickingIndividual i) {
        return this.fitness == i.getFitness() ? 0 : this.fitness < i.getFitness() ? 1 : -1;
    }

    @Override
    public PickingIndividual clone() {
        return new PickingIndividual(this);
    }

    @Override
    public Item[] getGenome(int agent) {
        return this.genome;
    }

    public Item[] getGenome(GraphNode agent) {
        return this.genome;
    }

    @Override
    public void replaceFromChild(GraphNode agent, List<Item> genome) {
        this.genome = genome.toArray(this.genome);
    }

    @Override
    public void setGene(Integer agent, Integer index, Item value) {
        this.genome[index] = value;
    }

    @Override
    public int getNumGenes(int agent) {
        return this.genome.length;
    }

    @Override
    public GraphNode getAgent(int agentIdx) {
        return null;
    }
}
