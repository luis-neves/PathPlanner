package ga.geneticOperators;

import ga.Individual;
import picking.HybridPickingIndividual;

public abstract class Recombination<I extends Individual> extends GeneticOperator {

    public Recombination(double probability) {
        super(probability);
    }

    public abstract void run(I ind1, I ind2);

    public Recombination<HybridPickingIndividual> makeHybrid() {
        return (Recombination<HybridPickingIndividual>) this;
    }
}