package genetics;

import util.WTFException;

import java.util.*;

/**
 *
 * Created by Chris on 3/26/2016.
 */
public class EliteSelector {


    public static List<Genome> selectElites(double percentageElites, double probability, List<Genome> genomes_){
        List<Genome> elites = new ArrayList<>();

        List<Genome> genomes = new ArrayList<>(genomes_);
        Collections.sort(genomes);

        Genome firstElite = getElite(probability,genomes);
        genomes.remove(firstElite);
        elites.add(firstElite);



        Map<Genome,Double> combinedScores = new HashMap<>();
        while(elites.size() < percentageElites*genomes_.size()) {
            combinedScores.clear();
            genomes.forEach(g -> combinedScores.put(g, getGeneticVectorDistance(g, elites) + g.getFitness()));

            Collections.sort(genomes, (ge1, ge2) -> {
                double ge1Score = combinedScores.get(ge1);
                double ge2Score = combinedScores.get(ge2);
                if (ge1Score < ge2Score) return 1;
                if (ge1Score > ge2Score) return -1;
                return 0;
            });

            Genome elite = getElite(probability,genomes);
            genomes.remove(elite);
            elites.add(elite);
        }


        return elites;
    }



    private static double getGeneticVectorDistance(Genome g, List<Genome> elites) {
        double dist = 0;
        for( Genome elite : elites )
            dist += g.distanceTo(elite);
        return dist;
    }



    public static Genome getElite(double probability, List<Genome> genomes){
        int index = 0;
        double rand = Math.random();
//        System.out.println(probability);
        while( index < genomes.size() ) {
            if (rand < probability)
                return genomes.get(index);
            probability += probability*(1-probability);
//            System.out.println(probability);
            index++;
        }

        throw new WTFException("Could not select the first elite!");
    }
}
