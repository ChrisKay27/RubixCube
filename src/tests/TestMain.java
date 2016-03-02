package tests;

import experiment.ExperimentResultsWriter;
import tests.searches.TestSearch;
import training.TrainingDataGenerator.TestTrainingDataGenerator;

import static cube.RubixCube.RubixCubeTester;

/**
 *
 * Created by Chris on 1/12/2016.
 */
public class TestMain {
    public static void main(String[] args) {
        boolean success, tmpSuccess;


        tmpSuccess = RubixCubeTester.test();
        System.out.println("Testing RubixCube results: " + tmpSuccess);
        success = tmpSuccess;

        tmpSuccess = TestTrainingDataGenerator.test();
        System.out.println("Testing training data generator, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = TestSearch.test();
        System.out.println("Testing Search, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = ExperimentResultsWriter.TestExperimentResultsWriter.test();
        System.out.println("Testing Expreiments Results Writer, results: " + tmpSuccess);
        success &= tmpSuccess;

        System.out.println("\nUnit Test results:" + success);
    }
}
