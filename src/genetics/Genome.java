package genetics;

import java.util.*;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class Genome implements Comparable<Genome> {

    private List<Gene> genes = new ArrayList<>();
    private double fitness;

    public Genome() {
    }

    public Genome(List<Gene> genes) {
        genes.forEach(gene -> genes.add(gene.getCopy()));
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double distanceTo(Genome elite) {
        double dist = 0;
        for (int i = 0; i < genes.size(); i++) {
            dist += elite.genes.get(i).distanceTo(genes.get(i));
        }
        return dist;
    }

    public Genome copy(){
        return new Genome(genes);
    }

    public void mutate(double percentOfGenome) {
        List<Gene> nGenes = new ArrayList<>(genes);
        Collections.shuffle(nGenes);

        for (int i = 0; i < nGenes.size() * percentOfGenome; i++)
            nGenes.get(i).mutate();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == this ) return true;
        if( !(obj instanceof Genome) ) return false;
        Genome g = (Genome) obj;

        if( genes.size() != g.genes.size() ) return false;

        for (int i = 0; i < genes.size(); i++)
            if( !genes.get(i).equals(g.genes.get(i)))
                return false;

        return true;
    }

    @Override
    public int compareTo(Genome o) {
        if( fitness > o.fitness ) return 1;
        if( fitness < o.fitness ) return -1;
        return 0;
    }

    public Genome getRandomCopy() {
        return copy();
    }
}
