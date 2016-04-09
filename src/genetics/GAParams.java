package genetics;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class GAParams {
    private double percElites;
    private double percMutations;
    private double percCross;
    private int generations;
    private int popSize;
    private Function<Genome,Double> fitTest;
    private Genome sampleGenome;
    private double acceptableFitness;

    public GAParams() {
    }

    public double getPercElites() {
        return percElites;
    }

    public void setPercElites(double percElites) {
        this.percElites = percElites;
    }

    public double getPercMutations() {
        return percMutations;
    }

    public void setPercMutations(double percMutations) {
        this.percMutations = percMutations;
    }

    public double getPercCross() {
        return percCross;
    }

    public void setPercCross(double percCross) {
        this.percCross = percCross;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public Function<Genome, Double> getFitTest() {
        return fitTest;
    }

    public void setFitTest(Function<Genome, Double> fitTest) {
        this.fitTest = fitTest;
    }

    public Genome getSampleGenome() {
        return sampleGenome;
    }

    public void setSampleGenome(Genome sampleGenome) {
        this.sampleGenome = sampleGenome;
    }

    public double getAcceptableFitness() {
        return acceptableFitness;
    }

    public void setAcceptableFitness(double acceptableFitness) {
        this.acceptableFitness = acceptableFitness;
    }
}
