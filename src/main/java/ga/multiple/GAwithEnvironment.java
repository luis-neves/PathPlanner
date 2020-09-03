package ga.multiple;

import ga.GeneticAlgorithm;
import ga.Individual;
import utils.Graphs.EnvironmentNodeGraph;
import utils.Graphs.GraphNode;

public class GAwithEnvironment {
    EnvironmentNodeGraph environment;
    GeneticAlgorithm ga;
    Float[] genBestFitness;

    public Float[] getGenAvgFitness() {
        return genAvgFitness;
    }

    public void setGenAvgFitness(Float[] genAvgFitness) {
        this.genAvgFitness = genAvgFitness;
    }

    Float[] genAvgFitness;
    GraphNode lastAgent;

    public Float[] getGenBestFitness() {
        return genBestFitness;
    }

    public void setGenBestFitness(Float[] genBestFitness) {
        this.genBestFitness = genBestFitness;
    }

    public GraphNode getLastAgent() {
        return lastAgent;
    }

    public void setLastAgent(GraphNode lastAgent) {
        this.lastAgent = lastAgent;
    }

    public GAwithEnvironment(EnvironmentNodeGraph environment, GeneticAlgorithm ga) {
        this.environment = environment;
        this.ga = ga;
    }

    public GAwithEnvironment() {
    }

    public EnvironmentNodeGraph getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentNodeGraph environment) {
        this.environment = environment;
    }

    public GeneticAlgorithm getGa() {
        return ga;
    }

    public void setGa(GeneticAlgorithm ga) {
        this.ga = ga;
    }

    public void addGenFitValue(int generation, Individual bestInRun) {
        this.genBestFitness[generation] = (float) bestInRun.getFitness();
    }
    public void addAvgFitValue(int generation, float avg) {
        this.genAvgFitness[generation] = avg;
    }

}
