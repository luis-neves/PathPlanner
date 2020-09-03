package ga.geneticOperators;

import ga.Individual;
import picking.HybridPickingIndividual;

public abstract class Mutation<I extends Individual> extends GeneticOperator {

    public Mutation(double probability) {
        super(probability);
    }

    public abstract void run(I individual);

    public Mutation<HybridPickingIndividual> makeHybrid() {
        return (Mutation<HybridPickingIndividual>) this;
    }
}
