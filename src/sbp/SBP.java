package sbp;

import neuralnet.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Chris on 2/9/2016.
 */
public class SBP {

    public static SBPResults runExperiment(SBPParams params, SBPImpl sbpImpl, List<TrainingTuple> trainingTuplesMasterList){

        //Take a sub set of the training tuples (network doesn't perform well with 30000 training tuples)
        List<TrainingTuple> trainingTuples = new ArrayList<>();
        trainingTuples.clear();
        for (int i = 0; i < 5000; i++)
            trainingTuples.add(trainingTuplesMasterList.remove((int)(Math.random()*trainingTuplesMasterList.size())));
        trainingTuplesMasterList.addAll(trainingTuples);


        //local to keep track of the best network so far
        SBPImpl currentBest = sbpImpl.copy();

        //locals to save having params.get~ everywhere
        double N = params.getN();
        Function<Double,Double> deriv_sigmoid = params.getDeriv_sigmoid();
        Runnable sbpListener = params.getSBPListener();

        Map<Neuron,Double> deltas = new HashMap<>();

        for (int epoc = 0; epoc < params.getEpocs(); epoc++) {

            deltas.clear();

            for (int i = 0; i < params.getTrainingIterations(); i++) {
                deltas.clear();

                //Apply training tuple to the SBPImpl and get the output
                TrainingTuple tt = trainingTuples.get((int)(Math.random()*trainingTuples.size()));
                List<Double> expectedOutput = tt.getExpectedOutputs();
                List<Double> actualOutput = sbpImpl.feedForward(tt.getInputs());


                //Calculate DeltaK and DeltaKJ
                List<Neuron> outputNeurons = sbpImpl.getOutputNeurons();
                for (int o = 0; o < outputNeurons.size(); o++) {
                    Neuron output = outputNeurons.get(o);

                    double deltaK = deltaK(expectedOutput.get(o),actualOutput.get(o),output.getNet(), deriv_sigmoid);
                    deltas.put(output,deltaK);


                    List<Edge> inputEdges = output.getInputEdges();

                    for (int in = 0; in < inputEdges.size(); in++) {
                        Edge inputEdge = inputEdges.get(in);

                        double deltaKJ = deltaKj(deltaK, inputEdge.getSource().getAct(), N);

                        inputEdge.setDeltaWeight(deltaKJ);
                    }
                }


                //Calculate DeltaJ and DeltaJI
                final List<List<Neuron>> hiddenLayers = sbpImpl.getHiddenLayers();
                for (int hidLayInd = hiddenLayers.size()-1; hidLayInd >= 0 ; --hidLayInd) {
                    List<Neuron> hiddenNeurons = hiddenLayers.get(hidLayInd);
                    for (Neuron hidden : hiddenNeurons) {
                        double deltaJ = deltaJ(hidden, deltas,deriv_sigmoid);

                        deltas.put(hidden, deltaJ);

                        List<Edge> inputEdges = hidden.getInputEdges();

                        double weightDecay = params.getWeightDecay();
                        for (int in = 0; in < inputEdges.size(); in++) {
                            Edge inputEdge = inputEdges.get(in);
                            double deltaJI = deltaJi(deltaJ, inputEdge.getSource().getAct(),N);

                            inputEdge.setDeltaWeight(deltaJI - deltaJI*weightDecay*N);
                        }
                    }
                }

                //Apply edge weight updates
                sbpImpl.getOutputNeurons().forEach(n -> n.getInputEdges().forEach(e->e.applyDeltaWeight(params.getAlpha())));
                hiddenLayers.forEach(hiddenNeurons->hiddenNeurons.forEach(n -> n.getInputEdges().forEach(e->e.applyDeltaWeight(params.getAlpha()))));

                //Something likes to listen to the SBP algorithm I guess
                if(sbpListener != null)
                    sbpListener.run();


                //Calc network error
                double networkError = calculateNetworkError(sbpImpl, trainingTuples, sbpImpl.getNetworkError());
                sbpImpl.setNetworkError(networkError);


                //Keep track of the best NN
                if( currentBest.getNetworkError() > sbpImpl.getNetworkError() ){

                    if( currentBest.getNetworkError() > sbpImpl.getNetworkError() ) {
                        System.out.println("Found a better Network, prev error:" + currentBest.getNetworkError() + " new error:" + sbpImpl.getNetworkError());
                        currentBest = sbpImpl.copy();
                    }
                }

                //If the network error is below a desired amount then just return
                if( networkError < params.getDesiredErrorRate() || params.stopFlag() ){

                    sbpImpl = currentBest;
                    SBPResults sbpResults = new SBPResults(((epoc)*params.getTrainingIterations())+(i+1),epoc+1,currentBest.getNetworkError(),currentBest);

                    return sbpResults;
                }

                if( i % 100 == 0)
                    System.out.println("Done iteration " + i);
            }

            System.out.println("Epoch " + epoc + " over, error: " + currentBest.getNetworkError());

            //Reinit sbpImpl for the next epoch
            sbpImpl.init();
        }

        sbpImpl = currentBest;
        SBPResults sbpResults = new SBPResults((params.getEpocs())*(params.getTrainingIterations()),params.getEpocs(),currentBest.getNetworkError(),currentBest);

        return sbpResults;
    }

    public static double deltaK(double T, double Z, double net, Function<Double,Double> deriv_sigmoid) {
        return (T-Z)*deriv_sigmoid.apply(net);
    }

    public static Double deltaKj(double deltaK, double actJ, double N) {
        return N * deltaK * actJ;
    }


    public static double deltaJ(Neuron hidden, Map<Neuron, Double> deltas, Function<Double,Double> deriv_sigmoid) {
        //ActJ
        double t = deriv_sigmoid.apply(hidden.getNet());

        //Sum( WeightsOfOutputEdges*DeltaKAtOutput)
        double sumOfWeightMultErrors = 0;
        for(Edge e : hidden.getOutputEdges()){
            sumOfWeightMultErrors += e.getWeight()*deltas.get(e.getDest());
        }

        return t*sumOfWeightMultErrors;
    }

    public static double deltaJi(double deltaJ, double actI, double N) {
        return N*actI*deltaJ;
    }



    public static double calculateNetworkError(SBPImpl sbpImpl, List<TrainingTuple> trainingTuples, double previousNetworkError){
//        System.out.println("Number training tuples: " + trainingTuples.size());

        double networkError = 0;
        for (TrainingTuple TT : trainingTuples){

            List<Double> expectOutputs = TT.getExpectedOutputs();
            List<Double> actOutput = sbpImpl.feedForward(TT.getInputs());

            double vectorDistance = 0;
            for (int outputs = 0; outputs < expectOutputs.size(); outputs++) {
                vectorDistance += Math.pow(expectOutputs.get(outputs) - actOutput.get(outputs), 2);

            }
            vectorDistance /= 2;
            networkError += vectorDistance;

            if( networkError > previousNetworkError )
                return networkError;
        }
        return networkError;
    }

    public static class SBPResults{
        public final int numberOfIterationsTaken;
        public final int numberOfEpochs;
        public final double networkError;
        public final SBPImpl sbpImpl;

        public SBPResults(int numberOfIterationsTaken, int numberOfEpochs, double networkError, SBPImpl sbpImpl) {
            this.numberOfIterationsTaken = numberOfIterationsTaken;
            this.numberOfEpochs = numberOfEpochs;
            this.networkError = networkError;
            this.sbpImpl = sbpImpl;
        }
    }
}
