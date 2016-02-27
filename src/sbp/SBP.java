package sbp;

import neuralnet.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class SBP {



    public static SBPResults runExperiment(SBPParams params, SBPImpl sbpImpl, List<TrainingTuple> trainingTuples){

        SBPImpl currentBest = sbpImpl;

        double N = params.getN();
        Function<Double,Double> deriv_sigmoid = params.getDeriv_sigmoid();
        Runnable sbpListener = params.getSBPListener();

        int count=0;
        for (int epoc = 0; epoc < params.getEpocs(); epoc++) {
//            System.out.println("Starting epoc " + epoc);

            Map<Neuron,Double> deltas = new HashMap<>();

            for (int i = 0; i < params.getTrainingIterations(); i++) {
                //System.out.println("Starting run " + i);
                deltas.clear();

                TrainingTuple tt = trainingTuples.get((int)(Math.random()*trainingTuples.size()));
                List<Double> expectedOutput = tt.getExpectedOutputs();
                List<Double> actualOutput = sbpImpl.feedForward(tt.getInputs());


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


                final List<List<Neuron>> hiddenLayers = sbpImpl.getHiddenLayers();


                for (int hidLayInd = hiddenLayers.size()-1; hidLayInd >= 0 ; --hidLayInd) {
                    List<Neuron> hiddenNeurons = hiddenLayers.get(hidLayInd);
                    for (Neuron hidden : hiddenNeurons) {
                        double deltaJ = deltaJ(hidden, deltas,deriv_sigmoid);

                        deltas.put(hidden, deltaJ);

                        List<Edge> inputEdges = hidden.getInputEdges();

                        for (int in = 0; in < inputEdges.size(); in++) {
                            Edge inputEdge = inputEdges.get(in);
                            double deltaJI = deltaJi(deltaJ, inputEdge.getSource().getAct(),N);

                            inputEdge.setDeltaWeight(deltaJI);
                        }
                    }
                }


                sbpImpl.getOutputNeurons().forEach(n -> n.getInputEdges().forEach(e->e.applyDeltaWeight(params.getAlpha())));
                hiddenLayers.forEach(hiddenNeurons->hiddenNeurons.forEach(n -> n.getInputEdges().forEach(e->e.applyDeltaWeight(params.getAlpha()))));


                double networkError = 0;
                for (TrainingTuple TT : trainingTuples){

                    List<Double> expectOutputs = TT.getExpectedOutputs();
                    List<Double> actOutput = sbpImpl.feedForward(TT.getInputs());

                    double vectorDistance = 0;
                    for (int outputs = 0; outputs < expectOutputs.size(); outputs++)
                        vectorDistance += Math.pow(expectOutputs.get(outputs)-actOutput.get(outputs),2);
                    vectorDistance /= 2;
                    networkError += vectorDistance;
                }

                sbpImpl.setNetworkError(networkError);

                if(sbpListener != null)
                    sbpListener.run();


//                count++;
//                if( count % 100 == 0 )
//                    System.out.println("Inputs: " + tt.getInputs().get(0) + "," + tt.getInputs().get(1) + " Expected output: "+tt.getExpectedOutputs().get(0)+" Actual output: "+actualOutput.get(0)+" Network error: " + networkError );

                if( currentBest.getNetworkError() > sbpImpl.getNetworkError() ){
                    currentBest = sbpImpl.copy();
                }

                if( networkError < params.getDesiredErrorRate() ){
//                    System.out.println("Success! after " + ((i+1)*(epoc+1)) + " runs");
//                    System.out.println("Network error: " + networkError );
                    SBPResults sbpResults = new SBPResults(((epoc)*params.getTrainingIterations())+(i+1),epoc+1,sbpImpl.getNetworkError(),sbpImpl);

                    return sbpResults;
                }
            }
            sbpImpl.init();
        }

        SBPResults sbpResults = new SBPResults((params.getEpocs())*(params.getTrainingIterations()),params.getEpocs(),sbpImpl.getNetworkError(),sbpImpl);

        return sbpResults;
    }

    public static double deltaK(double T, double Z, double net, Function<Double,Double> deriv_sigmoid) {
        return (T-Z)*deriv_sigmoid.apply(net);
    }

    public static Double deltaKj(double deltaK, double actJ, double N) {
//        if( biasEdge ) System.out.println("deltaKj Bias neuron");
//        else System.out.println("deltaKj non-bias neuron");
        return N * deltaK * actJ;
    }

    public static double deltaJ(Neuron hidden, Map<Neuron, Double> deltas, Function<Double,Double> deriv_sigmoid) {
        double t = deriv_sigmoid.apply(hidden.getNet());

        double sumOfWeightMultErrors = 0;
        for(Edge e : hidden.getOutputEdges()){
            sumOfWeightMultErrors += e.getWeight()*deltas.get(e.getDest());
        }

        return t*sumOfWeightMultErrors;
    }

    public static double deltaJi(double deltaJ, double actI, double N) {
        return N*actI*deltaJ;
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
