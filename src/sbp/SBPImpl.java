package sbp;

import neuralnet.Neuron;

import java.util.List;

/**
 * Created by chris_000 on 2/23/2016.
 */
public interface SBPImpl {
    void init();

    List<Double> feedForward(List<Double> inputs);

    List<Neuron> getOutputNeurons();

    List<List<Neuron>> getHiddenLayers();

    void setNetworkError(double networkError);

    double getNetworkError();

    SBPImpl copy();
}
