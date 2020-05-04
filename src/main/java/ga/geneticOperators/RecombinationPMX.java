package ga.geneticOperators;

import ga.GeneticAlgorithm;
import ga.Individual;
import picking.Item;

import java.util.*;

public class RecombinationPMX <I extends Individual> extends Recombination<I>  {
    public RecombinationPMX(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {
        Random rng = GeneticAlgorithm.random;
        int point1 = rng.nextInt(ind1.getNumGenes());
        int point2 = rng.nextInt(ind1.getNumGenes());

        int length = point2 - point1;
        if (length < 0)
        {
            length += ind1.getNumGenes();
        }
        List<Item> offspring1 = new ArrayList<Item>(Arrays.asList(ind1.getGenome()));
        List<Item> offspring2 = new ArrayList<Item>(Arrays.asList(ind2.getGenome()));

        Map<Item, Item> mapping1 = new HashMap<Item, Item>(length * 2);
        Map<Item, Item> mapping2 = new HashMap<Item, Item>(length * 2);
        for (int i = 0; i < length; i++)
        {
            int index = (i + point1) % ind1.getNumGenes();
            Item item1 = offspring1.get(index);
            Item item2 = offspring2.get(index);
            offspring1.set(index, item2);
            offspring2.set(index, item1);
            mapping1.put(item1, item2);
            mapping2.put(item2, item1);
        }

        offspring1 = checkUnmappedElements(offspring1, mapping2, point1, point2);
        offspring2 = checkUnmappedElements(offspring2, mapping1, point1, point2);

        ind1.replaceFromChild(offspring2);
        ind2.replaceFromChild(offspring1);
    }
    private List<Item> checkUnmappedElements(List<Item> offspring,
                                       Map<Item, Item> mapping,
                                       int mappingStart,
                                       int mappingEnd)
    {
        for (int i = 0; i < offspring.size(); i++)
        {
            if (!isInsideMappedRegion(i, mappingStart, mappingEnd))
            {
                Item mapped = offspring.get(i);
                while (mapping.containsKey(mapped))
                {
                    mapped = mapping.get(mapped);
                }
                offspring.set(i, mapped);
            }
        }
        return offspring;
    }

    private boolean isInsideMappedRegion(int position,
                                         int startPoint,
                                         int endPoint)
    {
        boolean enclosed = (position < endPoint && position >= startPoint);
        boolean wrapAround = (startPoint > endPoint && (position >= startPoint || position < endPoint));
        return enclosed || wrapAround;
    }

//    protected List<List<Item>> mate(List<Item> parent1,
//                                    List<Item> parent2,
//                                    int numberOfCrossoverPoints,
//                                    Random rng)
//    {
//        assert numberOfCrossoverPoints == 2 : "Expected number of cross-over points to be 2.";
//
//        if (parent1.size() != parent2.size())
//        {
//            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
//        }
//
//        List<Item> offspring1 = new ArrayList<Item>(parent1); // Use a random-access list for performance.
//        List<Item> offspring2 = new ArrayList<Item>(parent2);
//
//        int point1 = rng.nextInt(parent1.size());
//        int point2 = rng.nextInt(parent1.size());
//
//        int length = point2 - point1;
//        if (length < 0)
//        {
//            length += parent1.size();
//        }
//
//        Map<T, T> mapping1 = new HashMap<T, T>(length * 2); // Big enough map to avoid re-hashing.
//        Map<T, T> mapping2 = new HashMap<T, T>(length * 2);
//        for (int i = 0; i < length; i++)
//        {
//            int index = (i + point1) % parent1.size();
//            T item1 = offspring1.get(index);
//            T item2 = offspring2.get(index);
//            offspring1.set(index, item2);
//            offspring2.set(index, item1);
//            mapping1.put(item1, item2);
//            mapping2.put(item2, item1);
//        }
//
//        checkUnmappedElements(offspring1, mapping2, point1, point2);
//        checkUnmappedElements(offspring2, mapping1, point1, point2);
//
//        List<List<Item>> result = new ArrayList<List<Item>>(2);
//        result.add(offspring1);
//        result.add(offspring2);
//        return result;
//    }
    @Override
    public String toString(){
        return "PMX recombination (" + probability + ")";
    }
}
