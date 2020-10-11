package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.Individual;
import picking.HybridPickingIndividual;
import picking.Item;
import utils.Graphs.GraphNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecombinationCX<I extends Individual> extends Recombination<I> {

    public RecombinationCX(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {
        List<Item> genome1 = null;
        List<Item> genome2 = null;
        GraphNode agent = null;
        if (ind1 instanceof HybridPickingIndividual) {
            agent = ind1.getAgent(GeneticAlgorithm.random.nextInt(ind1.getNumGenes()));
            genome1 = Arrays.asList(ind1.getGenome(agent));
            genome2 = Arrays.asList(ind2.getGenome(agent));
        } else {
            genome1 = Arrays.asList(ind1.getGenome(-1));
            genome2 = Arrays.asList(ind2.getGenome(-1));
        }
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

            ind1.replaceFromChild(agent, child);
            ind2.replaceFromChild(agent, child);
        } else {

        }
    }


    @Override
    public String toString() {
        return "Cycle Crossover (" + probability + ")";
    }
}