package sbp;

import neuralnet.*;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Chris on 3/7/2016.
 */
public class SBPNNExperiment {


    private List<TrainingTuple> trainingTuples;

    private SBPParams sbpParams;

    private double A = 1.716;
    private double B = 0.667;
    private double N = 0.125;
    private double alpha;

    private int epochs;
    private int trainingIterations;

    private int inputs;
    private int hiddenLayers;
    private int[] hiddenNeuronsPerLayer;
    private int outputs;

    private final double desiredErrorRate;

    private boolean usingBias = true;
    private NeuralNet neuralNet;


    private Function<Double,Double> sigmoid = net -> A*Math.tanh(B*net);
    private Function<Double,Double> deriv_sigmoid = net -> A*B*(1-Math.pow(Math.tanh(B*net),2));

    private Consumer<SBP.SBPState> updateListener;


    public SBPNNExperiment(NNExperimentParams params) {
        A = params.getA();
        B = params.getB();
        N = params.getN();
        this.usingBias = params.isUsingBias();
        this.hiddenLayers = params.getHiddenLayers();
        inputs = params.getInputs();
        this.desiredErrorRate = params.getDesiredErrorRate();
        this.hiddenNeuronsPerLayer = params.getHiddenNeuronsPerLayer();
        outputs = params.getOutputs();
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
        params.setOutputNeurons(outputs);
        params.setSigmoid(sigmoid);
        neuralNet = new NeuralNet(params);
        neuralNet.init();
//        System.out.println("params.getInputNeurons() = " + params.getInputNeurons());
    }


    public SBP.SBPResults run() {

        sbpParams = new SBPParams();
        sbpParams.setN(N);
        sbpParams.setDeriv_sigmoid(deriv_sigmoid);
        sbpParams.setSBPListener(sbpState -> {if( updateListener != null ) updateListener.accept(sbpState);});
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


    public void setUpdateListener(Consumer<SBP.SBPState> updateListener) {
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
