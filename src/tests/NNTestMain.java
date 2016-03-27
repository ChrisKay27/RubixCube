package tests;

import tests.neuralnet.NeuralNetTest;
import tests.sbp.SBPTest;

import java.util.function.Consumer;

/**
 *
 * Created by Chris on 1/12/2016.
 */
public class NNTestMain {
    public static void main(Consumer<String> out) {
        boolean success;
        boolean tmpSuccess;

        tmpSuccess = NeuralNetTest.testInit();
        out.accept("Testing NeuralNet.init() result:" + tmpSuccess);
        success = tmpSuccess;

        tmpSuccess = NeuralNetTest.testFeedForward();
        out.accept("Testing NeuralNet.feedForward() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaK() ;
        out.accept("Testing SBP.deltaK() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaKj();
        out.accept("Testing SBP.deltaKj() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaJ() ;
        out.accept("Testing SBP.deltaJ() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaJi();
        out.accept("Testing SBP.deltaJi() result:" + tmpSuccess);
        success &= tmpSuccess;

        out.accept("\nNN Unit Test results: " + success);
    }
}
