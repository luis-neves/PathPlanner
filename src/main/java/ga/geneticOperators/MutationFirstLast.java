package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.VectorIndividual;
import picking.Item;

public class MutationFirstLast<I extends VectorIndividual> extends Mutation<I> {

    public MutationFirstLast(double probability) {
        super(probability);
    }

    @Override
    public void run(I individual) {
        int indSize = individual.getNumGenes();
        if (GeneticAlgorithm.random.nextDouble() < probability) {
            for (int i = 0; i < indSize; i++) {
                if (i < indSize-1) {
                    Item next = individual.getGene(i + 1);
                    Item actual = individual.getGene(i);
                    individual.setGene(i, next);
                    individual.setGene(i + 1, actual);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "First to Last mutation (" + probability + ")";
    }
}
