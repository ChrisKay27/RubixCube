package tests;

import tests.neuralnet.NeuralNetTest;
import tests.sbp.SBPTest;

/**
 *
 * Created by Chris on 1/12/2016.
 */
public class NNTestMain {
    public static void main(String[] args) {
        boolean success;
        boolean tmpSuccess;

        tmpSuccess = NeuralNetTest.testInit();
        System.out.println("Testing NeuralNet.init() result:" + tmpSuccess);
        success = tmpSuccess;

        tmpSuccess = NeuralNetTest.testFeedForward();
        System.out.println("Testing NeuralNet.feedForward() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaK() ;
        System.out.println("Testing SBP.deltaK() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaKj();
        System.out.println("Testing SBP.deltaKj() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaJ() ;
        System.out.println("Testing SBP.deltaJ() result:" + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = SBPTest.testDeltaJi();
        System.out.println("Testing SBP.deltaJi() result:" + tmpSuccess);
        success &= tmpSuccess;

        System.out.println("\nNN Unit Test results: " + success);
    }
}
