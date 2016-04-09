package tests.genetics;

import java.util.function.Consumer;

/**
 *
 * Created by Chris on 1/12/2016.
 */
public class TestGenetics {
    public static boolean main(Consumer<String> out) {
        boolean success, tmpSuccess;
        
        tmpSuccess = TestEliteSelector.testEliteSelection();
        out.accept("Testing Elite Selector, results: " + tmpSuccess);
        success = tmpSuccess;

        tmpSuccess = TestCrossOver.testCrossOver();
        out.accept("Testing Cross Over, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = TestGeneticAlgorithm.testGeneticAlgorithms();
        out.accept("Testing Genetic Algorithm, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = TestMutator.testMutator();
        out.accept("Testing Mutator, results: " + tmpSuccess);
        success &= tmpSuccess;

        out.accept("\nGenetic Unit Test results:" + success);
        return success;
    }
}
