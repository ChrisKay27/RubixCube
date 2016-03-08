package neuralnet;

import util.WTFException;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class NeuralNetParams implements Serializable {

    private int inputNeurons;
    private int outputNeurons;
    private boolean biasNeuron;

    //Lambda functions don't serialize well...
    private transient Function<Double,Double> sigmoid;

    private int numberOfHiddenLayers;
    private int[] hiddenNeuronsInLayer;



    public int getInputNeurons() {
        return inputNeurons;
    }

    public int getOutputNeurons() {
        return outputNeurons;
    }

    public boolean usingBiasNeuron() {
        return biasNeuron;
    }

    public Function<Double, Double> getSigmoid() {
        return sigmoid;
    }

    public void setInputNeurons(int inputNeurons) {
        this.inputNeurons = inputNeurons;
    }

    public void setOutputNeurons(int outputNeurons) {
        this.outputNeurons = outputNeurons;
    }

    public void setBiasNeuron(boolean biasNeuron) {
        this.biasNeuron = biasNeuron;
    }

    public void setSigmoid(Function<Double, Double> sigmoid) {
        this.sigmoid = sigmoid;
    }

    public int getNumberOfHiddenLayers() {
        return numberOfHiddenLayers;
    }

    public void setNumberOfHiddenLayers(int numberOfHiddenLayers) {
        this.numberOfHiddenLayers = numberOfHiddenLayers;
    }

    public int[] getHiddenNeuronsInLayer() {
        if (hiddenNeuronsInLayer==null)
            throw new WTFException("You have to set the number of hidden neurons in each layer.");
        return hiddenNeuronsInLayer;
    }

    public void setHiddenNeuronsInLayer(int... hiddenNeuronsInLayer) {
        this.hiddenNeuronsInLayer = hiddenNeuronsInLayer;
    }


}
