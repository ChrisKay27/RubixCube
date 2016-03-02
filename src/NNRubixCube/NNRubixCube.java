package NNRubixCube;

import neuralnet.*;
import sbp.SBP;
import sbp.SBP.SBPResults;
import sbp.SBPParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * Created by Chris on 2/9/2016.
 */
public class NNRubixCube {
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
    private int inputs;

    public NNRubixCube(NNExperimentParams params) {
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

        File f = new File("trainingData.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(f))){

            String tupleStr = br.readLine();

            String[] tupleSplit = tupleStr.split("\\|");

            String[] inputsSplit = tupleSplit[0].split(",");
            List<Double> inputs = new ArrayList<>();
            for(String inputStr : inputsSplit )
                inputs.add(Double.parseDouble(inputStr));


            List<Double> outputs = new ArrayList<>();
            String[] ouputsSplit = tupleSplit[1].split(",");
            for(String outputStr : ouputsSplit )
                outputs.add(Double.parseDouble(outputStr));

            System.out.println("input size " + inputs.size() + " outputs size: " + outputs.size());
            trainingTuples.add(new TrainingTuple(inputs,outputs));

        } catch (IOException e) {
            e.printStackTrace();
        }
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
