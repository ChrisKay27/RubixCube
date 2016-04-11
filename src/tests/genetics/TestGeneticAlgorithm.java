package tests.genetics;

import genetics.DoubleGene;
import genetics.Genome;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class TestGeneticAlgorithm {


    public static void main(String[] args) {
        System.out.println("Testing Genetic Algorithm");
        boolean passed = testGeneticAlgorithms();
        System.out.println("\nTesting of Genetic Algorithms: " +(passed?"Passed":"Fail"));
    }


    public static boolean testGeneticAlgorithms() {
        boolean passed;

        passed = TestEliteSelector.testEliteSelection();
        passed &= TestMutator.testMutator();
        passed &= TestCrossOver.testCrossOver();

        return passed;
    }


    public static List<Genome> getGenomes(int count){
        List<Genome> genomes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Genome g = new Genome();
            g.setFitness(i);
            for (int j = 0; j < 10; j++)
                g.getGenes().add(new DoubleGene(10-i));

            genomes.add(g);
        }

        return genomes;
    }
}
