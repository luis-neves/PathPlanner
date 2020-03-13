package ga;

import picking.Item;
import utils.Graphs.FitnessResults;
import utils.Graphs.GraphNode;

import java.util.HashMap;
import java.util.List;

public abstract class Individual<P extends Problem, I extends Individual> implements Comparable<I> {

    protected double fitness;
    protected int collisions;
    protected FitnessResults results;


    protected P problem;
    protected double picksPerAgent;

    public Individual(P problem) {
        this.problem = problem;
    }

    public Individual(Individual<P, I> original) {
        this.problem = original.problem;
        this.fitness = original.fitness;
        this.collisions = original.collisions;
        this.picksPerAgent = original.picksPerAgent;
        this.results = original.results;
    }

    public FitnessResults getResults() {
        return results;
    }

    public void setResults(FitnessResults results) {
        this.results = results;
    }

    public abstract double computeFitness();

    public abstract int getNumGenes();

    public abstract void swapGenes(I other, int index);

    public double getFitness() {
        return fitness;
    }
    public int getCollisions(){
        return collisions;
    }
    public double getPicksPerAgent() {
        return picksPerAgent;
    }

    public abstract int compareTo(I i);

    public abstract String printGenome();

    @Override
    public abstract I clone();

    public abstract Item getGene(int index);

    public abstract Item[] getGenome();

    public abstract void replaceFromChild(List<Item> genome);

    public abstract void setGene(Integer index, String value);


}
