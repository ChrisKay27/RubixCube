package xor;

import neuralnet.NNTrainingDataLoader;
import neuralnet.TrainingTuple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 4/9/2016.
 */
public class Xor {
    private static final List<TrainingTuple> trainingTuples;

    static{
        trainingTuples = NNTrainingDataLoader.loadTrainingTuples(new File("xor.csv"));
    }

    public static List<TrainingTuple> getTrainingTuples(){
        return new ArrayList<>(trainingTuples);
    }
}
