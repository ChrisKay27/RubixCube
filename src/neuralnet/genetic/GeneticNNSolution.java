package neuralnet.genetic;

import genetics.GAParams;
import genetics.GeneticAlgorithm;
import genetics.GeneticAlgorithm.GenerationResults;
import genetics.Genome;
import neuralnet.NNToGenomeConverter;
import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import neuralnet.TrainingTuple;
import sbp.SBP;
import xor.Xor;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Chris on 4/5/2016.
 */
public class GeneticNNSolution {

    private double A = 1.716;
    private double B = 0.667;
    private Function<Double,Double> sigmoid = net -> A*Math.tanh(B*net);

    public GeneticNNSolution(){

    }



    public NeuralNet run(GAParams gaParams, List<TrainingTuple> trainingTuples, Consumer<GenerationResults> listener){

        NeuralNetParams NNparams = new NeuralNetParams();
        NNparams.setBiasNeuron(true);
        NNparams.setInputNeurons(324);
        NNparams.setHiddenNeuronsInLayer(36);
        NNparams.setNumberOfHiddenLayers(1);
        NNparams.setOutputNeurons(7);
        NNparams.setSigmoid(sigmoid);


        NeuralNet neuralNet = new NeuralNet(NNparams);
        neuralNet.init();
        gaParams.setSampleGenome(NNToGenomeConverter.getGenome(neuralNet));


        gaParams.setFitTest((genome,generation)-> {
            NeuralNet nn = NNToGenomeConverter.getNeuralNet(genome, neuralNet);

//            System.out.println("fitness = " + fitness);
            return -SBP.calculateNetworkError(nn,trainingTuples);
        });

        GeneticAlgorithm ga = new GeneticAlgorithm(gaParams,listener);

        Genome elitest = ga.run();
        NeuralNet nn = NNToGenomeConverter.getNeuralNet(elitest);

        nn.setNetworkError(SBP.calculateNetworkError(nn,trainingTuples));
        double fitness = -nn.getNetworkError();
        System.out.println("Best Fitness: "+ fitness);
        return nn;
    }




    public static void main(String[] args) {
        new GeneticNNSolution().run(null,null,null);
    }


    public Function<Double, Double> getSigmoid() {
        return sigmoid;
    }
}
