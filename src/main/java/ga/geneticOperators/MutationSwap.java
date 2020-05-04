package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.VectorIndividual;
import picking.Item;

public class MutationSwap<I extends VectorIndividual> extends Mutation<I> {

    public MutationSwap(double probability) {
        super(probability);
    }

    @Override
    public void run(I individual) {
        int indSize = individual.getNumGenes();
        if (GeneticAlgorithm.random.nextDouble() < probability) {
            int index1,index2 = 0;
            do{
                index1 = GeneticAlgorithm.random.nextInt(indSize);
                index2 = GeneticAlgorithm.random.nextInt(indSize);
            }while(index1 == index2);
            Item item1 = individual.getGene(index1);
            Item item2 = individual.getGene(index2);
            individual.setGene(index2, item1);
            individual.setGene(index1, item2);
        }
    }

    @Override
    public String toString() {
        return "First to Last mutation (" + probability + ")";
    }
}
