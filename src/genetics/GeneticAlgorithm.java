package genetics;

import neuralnet.genetic.GeneticNNSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class GeneticAlgorithm {


    private final GAParams params;
    private final Consumer<GenerationResults> listener;
    private Genome elite;


    public GeneticAlgorithm(GAParams params, Consumer<GenerationResults> listener) {
        this.params = params;
        this.listener = listener;
    }


    public Genome run(){

        List<Genome> pop = getPopulation(params.getPopSize(),params.getSampleGenome());

        int count=0;
        int numKids = (int) (params.getPopSize()*params.getPercCross());
        int numFreaks = (int) (params.getPopSize()*params.getPercMutations());
        while(count++ < params.getGenerations()){

            int generation = count;
            //Fit test the genomes
            pop.forEach(genome -> genome.setFitness(params.getFitTest().apply(genome,generation)));
            //Prevent new untested mutants and kids to be added to the genome set before returning.
            if( count >= params.getGenerations() )
                break;

            //Test for an acceptable genome
            Collections.sort(pop);
            if( Math.abs(pop.get(0).getFitness()) < Math.abs(params.getAcceptableFitness()))
                break;


            System.out.printf("Generation %d over, best fitness:%.3f\n",count,pop.get(0).getFitness() );
            GenerationResults gr = new GenerationResults();
            gr.generation = generation;
            gr.bestFitness = pop.get(0).getFitness();
            gr.avgFitness = getAvgFitness(pop);
            gr.worstFitness = pop.get(pop.size()-1).getFitness();
            listener.accept(gr);


            pop = EliteSelector.selectElites(params.getPercElites(), 0.667, pop);

            List<Genome> kids = CrossOver.forceMating(numKids, pop);
            List<Genome> mutants = Mutator.getMutations(numFreaks, 0.20, pop);
            pop.addAll(kids);
            pop.addAll(mutants);
        }

        Collections.sort(pop);
        return pop.get(0);
    }


    private List<Genome> getPopulation(int popSize, Genome sampleGenome) {
        List<Genome> pop = new ArrayList<>();

        for (int i = 0; i < popSize; i++)
            pop.add(sampleGenome.getRandomCopy());

        return pop;
    }

    private double getAvgFitness(List<Genome> genomes){
        double avg = 0;
        for(Genome g : genomes )
            avg += g.getFitness();
        return avg/genomes.size();
    }


    public static class GenerationResults{
        public int generation;
        public double bestFitness;
        public double avgFitness;
        public double worstFitness;
    }
}
