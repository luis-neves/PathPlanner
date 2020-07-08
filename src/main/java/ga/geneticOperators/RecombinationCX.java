package ga.geneticOperators;

import ga.Individual;
import picking.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecombinationCX<I extends Individual> extends Recombination<I> {

    public RecombinationCX(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {

        List<Item> genome1 = Arrays.asList(ind1.getGenome(-1));
        List<Item> genome2 = Arrays.asList(ind2.getGenome(-1));
        if (!genome1.equals(genome2)) {
            List<Item> child = new ArrayList<>();
            List<Item> inCycle = new ArrayList<>();
            for (int i = 0; i < genome1.size(); i++) {
                if (!inCycle.contains(genome2.get(i))) {
                    List<Item> tempCycle = new ArrayList<>();
                    Item start = genome1.get(i);
                    Item temp = genome2.get(genome1.indexOf(start));
                    tempCycle.add(start);
                    tempCycle.add(temp);
                    inCycle.add(temp);
                    while (!temp.equals(start)) {
                        tempCycle.add(genome2.get(genome1.indexOf(temp)));
                        temp = genome2.get(genome1.indexOf(temp));
                        inCycle.add(temp);
                    }
                    tempCycle.remove(tempCycle.size() - 1);
                    child.addAll(tempCycle);
                }
            }

            ind1.replaceFromChild(null, child);
            ind2.replaceFromChild(null, child);
        } else {

        }
    }


    @Override
    public String toString() {
        return "Cycle Crossover (" + probability + ")";
    }
}