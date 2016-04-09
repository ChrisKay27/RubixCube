package genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class GeneticAlgorithm {


    private final GAParams params;
    private Genome elite;


    public GeneticAlgorithm(GAParams params) {
        this.params = params;
    }


    public Genome run(){

        List<Genome> pop = getPopulation(params.getPopSize(),params.getSampleGenome());

        int count=0;
        int numKids = (int) (params.getPopSize()*params.getPercCross());
        int numFreaks = (int) (params.getPopSize()*params.getPercMutations());
        while(count++ < params.getGenerations()){

            //Fit test the genomes
            pop.forEach(genome -> genome.setFitness(params.getFitTest().apply(genome)));
            //Prevent new untested mutants and kids to be added to the genome set
            if( count >= params.getGenerations() )
                break;

            //Test for an acceptable genome
            Collections.sort(pop);
            if( pop.get(0).getFitness() < params.getAcceptableFitness())

            pop = EliteSelector.selectElites(0.2, 0.667, pop);

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
}
