package xor.genetic;

import genetics.GAParams;
import genetics.GeneticAlgorithm;
import genetics.Genome;
import neuralnet.*;
import sbp.SBP;
import xor.Xor;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Chris on 4/5/2016.
 */
public class GeneticXorSolution {

    private double A = 1.716;
    private double B = 0.667;
    private Function<Double,Double> sigmoid = net -> A*Math.tanh(B*net);

    public GeneticXorSolution(){

    }

    public NeuralNet run(GAParams gaParams){

        NeuralNetParams NNparams = new NeuralNetParams();
        NNparams.setBiasNeuron(true);
        NNparams.setInputNeurons(2);
        NNparams.setHiddenNeuronsInLayer(2);
        NNparams.setNumberOfHiddenLayers(1);
        NNparams.setOutputNeurons(1);
        NNparams.setSigmoid(sigmoid);


        NeuralNet neuralNet = new NeuralNet(NNparams);
        neuralNet.init();
        gaParams.setSampleGenome(NNToGenomeConverter.getGenome(neuralNet));

        List<TrainingTuple> trainingTuples = Xor.getTrainingTuples();
        gaParams.setFitTest((genome,gen) -> {
            NeuralNet nn = NNToGenomeConverter.getNeuralNet(genome);
            double fitness = -SBP.calculateNetworkError(nn,trainingTuples);
            System.out.println("fitness = " + fitness);
            return fitness;
        });

        GeneticAlgorithm ga = new GeneticAlgorithm(gaParams, (gr)->{});

        Genome elitest = ga.run();
        NeuralNet nn = NNToGenomeConverter.getNeuralNet(elitest);
        double fitness = -SBP.calculateNetworkError(nn,trainingTuples);
        System.out.println(fitness);
        return nn;
    }




    public static void main(String[] args) {
        new GeneticXorSolution();
    }


    public Function<Double, Double> getSigmoid() {
        return sigmoid;
    }
}
