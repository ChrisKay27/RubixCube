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
        boolean success;
        success = RubixCubeTester.test();
        success &= TestTrainingDataGenerator.test();
        success &= TestSearch.test();
        success &= ExperimentResultsWriter.TestExperimentResultsWriter.test();

        System.out.println("Unit Test results:" + success);
    }
}
