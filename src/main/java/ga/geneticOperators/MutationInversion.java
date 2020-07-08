package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.Individual;
import ga.MultipleVectorIndividual;
import ga.VectorIndividual;
import picking.HybridClusterPicking;
import picking.HybridPickingIndividual;
import picking.Item;

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

        if (GeneticAlgorithm.random.nextDouble() < probability) {
            int indSize = 0;
            int agent = -1;
            if (individual.getClass().equals(HybridPickingIndividual.class)) {
                int numAgents = individual.getNumGenes();
                agent = GeneticAlgorithm.random.nextInt(numAgents - 1);
                indSize = individual.getNumGenes(agent);
            } else {
                indSize = individual.getNumGenes();
            }

            int index1, index2 = 0;
            do {
                index1 = GeneticAlgorithm.random.nextInt(indSize);
                index2 = GeneticAlgorithm.random.nextInt(indSize);
            } while (index1 == index2);
            if (index1 > index2) {
                int aux = 0;
                aux = index1;
                index1 = index2;
                index2 = aux;
            }
            List<Item> items = new ArrayList<>();
            for (int i = index1; i <= index2; i++) {
                items.add(individual.getGene(agent, i));
            }
            Collections.reverse(items);
            for (Item i : items) {
                individual.setGene(agent, index1 + items.indexOf(i), i);
            }
        }
    }

    @Override
    public String toString() {
        return "Inversion mutation (" + probability + ")";
    }
}
