package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.VectorIndividual;
import picking.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutationInversion<I extends VectorIndividual> extends Mutation<I> {

    public MutationInversion(double probability) {
        super(probability);
    }

    @Override
    public void run(I individual) {
        int indSize = individual.getNumGenes();
        if (GeneticAlgorithm.random.nextDouble() < probability) {
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
                items.add(individual.getGene(i));
            }
            Collections.reverse(items);
            for (Item i : items) {
                individual.setGene(index1 + items.indexOf(i), i);
            }
        }
    }

    @Override
    public String toString() {
        return "Inversion mutation (" + probability + ")";
    }
}
