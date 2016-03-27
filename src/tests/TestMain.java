package tests;

import experiment.ExperimentResultsWriter;
import tests.searches.TestSearch;
import training.TrainingDataGenerator.TestTrainingDataGenerator;

import java.util.function.Consumer;

import static cube.RubixCube.RubixCubeTester;

/**
 *
 * Created by Chris on 1/12/2016.
 */
public class TestMain {
    public static void main(Consumer<String> out) {
        boolean success, tmpSuccess;
        
        tmpSuccess = RubixCubeTester.test();
        out.accept("Testing RubixCube results: " + tmpSuccess);
        success = tmpSuccess;

        tmpSuccess = TestTrainingDataGenerator.test();
        out.accept("Testing training data generator, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = TestSearch.test();
        out.accept("Testing Search, results: " + tmpSuccess);
        success &= tmpSuccess;

        tmpSuccess = ExperimentResultsWriter.TestExperimentResultsWriter.test();
        out.accept("Testing Expreiments Results Writer, results: " + tmpSuccess);
        success &= tmpSuccess;

        out.accept("\nUnit Test results:" + success);
    }
}
