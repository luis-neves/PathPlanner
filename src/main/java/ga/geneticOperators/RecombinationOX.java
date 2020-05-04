package ga.geneticOperators;

import ga.Individual;
import picking.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecombinationOX<I extends Individual> extends Recombination<I> {

    public RecombinationOX(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {
        // get the size of the tours
        List<Item> genome1 = Arrays.asList(ind1.getGenome());
        List<Item> genome2 = Arrays.asList(ind1.getGenome());
        if (!genome1.equals(genome2)) {
            ArrayList<Item> child = new ArrayList<Item>();
            for (int i = 0; i < genome1.size(); i++) {
                child.add(null);
            }

            int size = (int) (Math.random() * (ind1.getNumGenes() - 1) + 1);
            int start = (int) (Math.random() * ((ind1.getNumGenes() - size) + 1));
            List<Item> subParent = genome1.subList(start, start + size);
            int i = 0;
            for (Item object : subParent) {
                child.set(start + i, object);
                i++;
            }

            for (Item gene1 : genome2) {
                if (!child.contains(gene1)) {
                    for (Item gene2 : child) {
                        if (gene2 == null) {
                            child.set(child.indexOf(gene2), gene1);
                            break;
                        }
                    }
                }
            }
            ind2.replaceFromChild(child);
        }
    }


    @Override
    public String toString() {
        return "Order one Crossover (" + probability + ")";
    }
}