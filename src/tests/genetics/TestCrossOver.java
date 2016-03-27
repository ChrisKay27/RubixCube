package tests.genetics;

import genetics.CrossOver;
import genetics.Genome;
import genetics.Mutator;

import java.util.List;

/**
 * Created by chris_000 on 3/26/2016.
 */
public class TestCrossOver {
    public static void main(String[] args) {
        testCrossOver();
    }

    public static boolean testCrossOver() {
        System.out.println("\nTesting Cross Over");
        boolean testGetChildren = testGetChildren();
        System.out.println("Testing Get Children : " + (testGetChildren?"Passed":"Fail"));

        return testGetChildren;
    }

    public static boolean testGetChildren() {

        List<Genome> elites = TestGeneticAlgorithm.getGenomes(10);

        int numFailures = 0;
        for (int i = 0; i < 10; i++) {
            List<Genome> kids = CrossOver.forceMating(i, elites);
            if( kids.size() != i )
                return false;

            //Check to make sure that the kids arent the same as the parent elites
            for(Genome kid : kids) {
                if (elites.contains(kid) )
                    if( numFailures++ > 1)
                        return false; //Allow 2 children to be identical to an elite. This should almost never happen, only when Math.random() returns less than 0.5 for every gene
            }
        }

        return true;
    }
}

