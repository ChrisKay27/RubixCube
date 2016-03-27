package genetics;

import java.util.ArrayList;
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
        while(count++ < params.getGenerations()){



        }



        return elite;
    }


    private List<Genome> getPopulation(int popSize, Genome sampleGenome) {
        List<Genome> pop = new ArrayList<>();

        for (int i = 0; i < popSize; i++) {
            pop.add(sampleGenome.getRandomCopy());
        }

        return pop;
    }
}
