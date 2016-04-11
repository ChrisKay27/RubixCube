package tests.genetics;

import genetics.Genome;
import genetics.Mutator;

import java.util.Collections;
import java.util.List;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class TestMutator {
    public static void main(String[] args) {
        testMutator();
    }

    public static boolean testMutator() {
        System.out.println("\nTesting Mutator");
        boolean testGetMutations = testGetMutations();
        System.out.println("Testing Get Mutations : " + (testGetMutations?"Passed":"Fail"));

        return testGetMutations;
    }

    public static boolean testGetMutations() {
        boolean passed = true;

        List<Genome> elites = TestGeneticAlgorithm.getGenomes(10);

        int failures = 0;
        for (int i = 0; i < 10; i++) {
            List<Genome> freaks = Mutator.getMutations(i, 0.5, elites);
            passed &= freaks.size() == i;

            boolean equal = elites.equals(freaks);
            if( equal ){
                if( failures > 1 ) //Allow for 2/10 failures, this should almost never happen, it will only happen if Math.random() returns 0.5 for every gene in a genome
                    passed = false;
                else
                    failures++;
            }
        }

        return passed;
    }
}















