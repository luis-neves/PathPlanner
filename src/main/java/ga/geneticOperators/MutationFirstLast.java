package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.Individual;
import ga.VectorIndividual;
import picking.HybridPickingIndividual;
import picking.Item;
import utils.Graphs.GraphNode;

public class MutationFirstLast<I extends Individual> extends Mutation<I> {

    public MutationFirstLast(double probability) {
        super(probability);
    }

    @Override
    public void run(I individual) {
        int indSize = 0;
        int agent_idx = -1;
        GraphNode agent = null;
        if (individual.getClass().equals(HybridPickingIndividual.class)) {
            int numAgents = individual.getNumGenes();
            agent_idx = GeneticAlgorithm.random.nextInt(numAgents - 1);
            indSize = individual.getNumGenes(agent_idx);
            agent = individual.getAgent(agent_idx);
        } else {
            indSize = individual.getNumGenes();
        }
        for (int i = 0; i < indSize; i++) {
            if (i < indSize - 1) {
                Item next = individual.getGene(agent, i + 1);
                Item actual = individual.getGene(agent, i);
                individual.setGene(agent, i, next);
                individual.setGene(agent, i + 1, actual);
            }
        }

    }

    @Override
    public String toString() {
        return "First to Last mutation (" + probability + ")";
    }
}
