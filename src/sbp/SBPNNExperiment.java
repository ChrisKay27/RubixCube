package sbp;

import neuralnet.NeuralNet;
import neuralnet.TrainingTuple;

import java.util.List;

/**
 * Created by Chris on 3/7/2016.
 */
public interface SBPNNExperiment {

    void init();
    SBP.SBPResults run();

    NeuralNet getNeuralNet();

    void setUpdateListener(Runnable updateListener);

    void setNeuralNet(NeuralNet neuralNet);

    double calcNetworkError(List<TrainingTuple> trainingTupleList);

    public void setTrainingTuples(List<TrainingTuple> trainingTuples);

    void stop();
}
