package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.Individual;
import ga.MultipleVectorIndividual;
import ga.VectorIndividual;
import picking.HybridClusterPicking;
import picking.HybridPickingIndividual;
import picking.Item;
import utils.Graphs.GraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MutationInversion<I extends Individual> extends Mutation<I> {

    public MutationInversion(double probability) {
        super(probability);
    }

    @Override
    public void run(I individual) {
        int indSize = 0;
        int agent_idx = -1;
        GraphNode agent = null;
        if (individual.getClass().equals(HybridPickingIndividual.class)) {
            int numAgents = individual.getNumGenes();
            agent_idx = GeneticAlgorithm.random.nextInt(numAgents);
            indSize = individual.getNumGenes(agent_idx);
            agent = individual.getAgent(agent_idx);
            if (indSize <= 1) {
                return;
            }
        } else {
            indSize = individual.getNumGenes();
            if (indSize <= 1) {
                return;
            }
        }

        int index1, index2 = 0;
        do {
            index1 = GeneticAlgorithm.random.nextInt(indSize);
            index2 = GeneticAlgorithm.random.nextInt(indSize);
        } while (index1 == index2 || index1 > index2);
        List<Item> items = new ArrayList<>();
        for (int i = index1; i <= index2; i++) {
            items.add(individual.getGene(agent, i));
        }
        Collections.reverse(items);
        for (Item i : items) {
            if (i == null) {
                System.out.println("item null");
            }
            individual.setGene(agent, index1 + items.indexOf(i), i);
        }

    }

    @Override
    public String toString() {
        return "Inversion mutation (" + probability + ")";
    }
}
