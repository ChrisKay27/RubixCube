package xor;

import neuralnet.*;
import sbp.SBP;
import sbp.SBP.SBPResults;
import sbp.SBPImpl;
import sbp.SBPParams;

import java.util.*;
import java.util.function.Function;

/**
 *
 * Created by Chris on 2/9/2016.
 */
public class XORProblem {
    private static List<TrainingTuple> trainingTuples;
    private double A = 1.716;//1;// 1.716;
    private double B = 0.667;//1;// 0.667;
    private double N = 0.125;//.5;//0.1;
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

    public XORProblem(NNExperimentParams params) {
        A = params.getA();
        B = params.getB();
        N = params.getN();
        this.usingBias = params.isUsingBias();
        this.hiddenLayers = params.getHiddenLayers();
        this.desiredErrorRate = params.getDesiredErrorRate();
        this.hiddenNeuronsPerLayer = params.getHiddenNeuronsPerLayer();
        epochs = params.getEpochs();
        trainingIterations = params.getTrainingIterationsPerEpoch();
        alpha = params.getAlpha();
    }

    public void init(){

        NeuralNetParams params = new NeuralNetParams();
        params.setBiasNeuron(usingBias);
        params.setInputNeurons(2);
        params.setNumberOfHiddenLayers(hiddenLayers);
        params.setHiddenNeuronsInLayer(hiddenNeuronsPerLayer);
        params.setOutputNeurons(1);
        params.setSigmoid(sigmoid);
        neuralNet = new NeuralNet(params);
        neuralNet.init();
    }


    public SBPResults run() {
        SBPParams sbpParams = new SBPParams();
        sbpParams.setN(N);
        sbpParams.setDeriv_sigmoid(deriv_sigmoid);
        sbpParams.setSBPListener(updateListener);
        sbpParams.setDesiredErrorRate(desiredErrorRate);
        sbpParams.setAlpha(alpha);
        sbpParams.setEpocs(epochs);
        sbpParams.setTrainingIterations(trainingIterations);

        final SBPResults sbpResults = SBP.runExperiment(sbpParams, neuralNet, trainingTuples);
        NeuralNet nn = (NeuralNet) sbpResults.sbpImpl;
        if( nn != null ) {
            NeuralNetIO.saveNN(nn);
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



    static{
        trainingTuples = new ArrayList<>();

        TrainingTuple tt;
        List<Double> trainingData;
        List<Double> expectedOutput;


        trainingData = new ArrayList<>(Arrays.asList(new Double[]{-1.0,-1.0}));
        expectedOutput = new ArrayList<>(Arrays.asList(new Double[]{-1.0}));
        tt = new TrainingTuple(trainingData,expectedOutput);
        trainingTuples.add(tt);


        trainingData = new ArrayList<>(Arrays.asList(new Double[]{-1.0,1.0}));
        expectedOutput = new ArrayList<>(Arrays.asList(new Double[]{1.0}));
        tt = new TrainingTuple(trainingData,expectedOutput);
        trainingTuples.add(tt);


        trainingData = new ArrayList<>(Arrays.asList(new Double[]{1.0,-1.0}));
        expectedOutput = new ArrayList<>(Arrays.asList(new Double[]{1.0}));
        tt = new TrainingTuple(trainingData,expectedOutput);
        trainingTuples.add(tt);


        trainingData = new ArrayList<>(Arrays.asList(new Double[]{1.0,1.0}));
        expectedOutput = new ArrayList<>(Arrays.asList(new Double[]{-1.0}));
        tt = new TrainingTuple(trainingData,expectedOutput);
        trainingTuples.add(tt);
    }

    /**
     * Used when loading a NN from disk
     */
    public void setNeuralNet(NeuralNet neuralNet) {
        this.neuralNet = neuralNet;

        //Lambda functions don't serialize well...
        neuralNet.getNNParams().setSigmoid(sigmoid);
    }
}
