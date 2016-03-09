package sbp;

import neuralnet.*;

import java.io.File;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Chris on 3/7/2016.
 */
public class SBPNNExperiment {

    private List<TrainingTuple> trainingTuples;
    private double A = 1.716;
    private double B = 0.667;
    private double N = 0.125;
    private double alpha;

    private int epochs;
    private int trainingIterations;
    private int hiddenLayers;
    private final double desiredErrorRate;

    private int[] hiddenNeuronsPerLayer;


    private boolean usingBias = true;
    private NeuralNet neuralNet;

    private Function<Double,Double> sigmoid = net -> A*Math.tanh(B*net);
    private Function<Double,Double> deriv_sigmoid = net -> A*(1-Math.pow(Math.tanh(B*net),2));

    private Runnable updateListener;
    private int inputs;
    private SBPParams sbpParams;

    public SBPNNExperiment(NNExperimentParams params) {
        A = params.getA();
        B = params.getB();
        N = params.getN();
        this.usingBias = params.isUsingBias();
        this.hiddenLayers = params.getHiddenLayers();
        inputs = params.getInputs();
        this.desiredErrorRate = params.getDesiredErrorRate();
        this.hiddenNeuronsPerLayer = params.getHiddenNeuronsPerLayer();
        epochs = params.getEpochs();
        trainingIterations = params.getTrainingIterationsPerEpoch();
        alpha = params.getAlpha();
    }

    public void init(){
        NeuralNetParams params = new NeuralNetParams();

        params.setBiasNeuron(usingBias);
        params.setInputNeurons(inputs);
        params.setNumberOfHiddenLayers(hiddenLayers);
        params.setHiddenNeuronsInLayer(hiddenNeuronsPerLayer);
        params.setOutputNeurons(7);
        params.setSigmoid(sigmoid);
        neuralNet = new NeuralNet(params);
        neuralNet.init();
        System.out.println("params.getInputNeurons() = " + params.getInputNeurons());
    }


    public SBP.SBPResults run() {

        sbpParams = new SBPParams();
        sbpParams.setN(N);
        sbpParams.setDeriv_sigmoid(deriv_sigmoid);
        sbpParams.setSBPListener(updateListener);
        sbpParams.setDesiredErrorRate(desiredErrorRate);
        sbpParams.setAlpha(alpha);
        sbpParams.setEpocs(epochs);
        sbpParams.setTrainingIterations(trainingIterations);

        final SBP.SBPResults sbpResults = SBP.runExperiment(sbpParams, neuralNet, trainingTuples);
        NeuralNet nn = (NeuralNet) sbpResults.sbpImpl;
        if( nn != null ) {
            neuralNet = nn;
            return sbpResults;
        }else
            System.out.println("Failure!");
        return sbpResults;
    }


    public NeuralNet getNeuralNet() {
        return neuralNet;
    }


    public void setUpdateListener(Runnable updateListener) {
        this.updateListener = updateListener;
    }



    /**
     * Used when loading a NN from disk
     */
    public void setNeuralNet(NeuralNet neuralNet) {
        this.neuralNet = neuralNet;

        //Lambda functions don't serialize well...
        neuralNet.getNNParams().setSigmoid(sigmoid);
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void setTrainingIterations(int trainingIterations) {
        this.trainingIterations = trainingIterations;
    }

    public void setA(double a) {
        this.A = a;
    }

    public void setB(double b) {
        this.B = b;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double calcNetworkError(List<TrainingTuple> trainingTupleList) {
        return SBP.calculateNetworkError(neuralNet,trainingTupleList, Double.MAX_VALUE);
    }

    public void setTrainingTuples(List<TrainingTuple> trainingTuples) {
        this.trainingTuples = trainingTuples;
    }

    public void stop() {
        if( sbpParams != null )
            sbpParams.setStopFlag(true);

    }
}
