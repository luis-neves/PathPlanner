package ga.selectionMethods;

import ga.GeneticAlgorithm;
import ga.Individual;
import ga.Population;
import ga.Problem;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.SortOrder;
import utils.ComparacaoDoubleDesc;
import utils.QuickSort;

import java.awt.geom.Arc2D;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Ranking<I extends Individual, P extends Problem<I>> extends SelectionMethod <I, P> {

    double[] accumulated;

    public Ranking(int popSize) {
        super(popSize);
        accumulated = new double[popSize];
    }

    @Override
    public Population<I, P> run(Population<I, P> original) {
        Population<I, P> result = new Population<>(original.getSize());


        int n = accumulated.length;
        for (int i = 0; i < popSize; i++) {
           accumulated[i] = original.getIndividual(i).getFitness();

        }

        // organizar aqui
        // codigo dos profs de AED
        QuickSort<Double> quickSort = new QuickSort<>(ComparacaoDoubleDesc.CRITERIO);
        quickSort.ordenar(accumulated);

        //Arrays.sort(accumulated, SortOrder.DESCENDING);
        //Collections.reverse(Arrays.asList(accumulated));

       // System.out.println("OLa mãe estou no rank selection");

        double ps = 1.1;// aquele documento diz que 1.1 é um bom valor
        for (int i = 0; i < popSize; i++) {
            accumulated[i] = (2-ps+2*(ps-1)*((i-1)/(n-1)))/n;
           // System.out.print(accumulated[i]+" ");
        }
        //System.out.println("END");

        for (int i = 0; i < popSize; i++) {
            result.addIndividual(roulette(original));
        }

        return result;
    }

    private I roulette(Population<I, P> population) {
        double probability = GeneticAlgorithm.random.nextDouble();

        for (int i = 0; i < popSize; i++) {
            if (probability <= accumulated[i]) {
                return (I) population.getIndividual(i).clone();
            }
        }

        //For the case where all individuals have fitness 0
        return (I) population.getIndividual(GeneticAlgorithm.random.nextInt(popSize)).clone();
    }
    
    @Override
    public String toString(){
        return "Ranking";
    }    
}
