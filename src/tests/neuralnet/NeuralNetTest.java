package tests.neuralnet;


import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import neuralnet.Neuron;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by chris_000 on 2/23/2016.
 */
public class NeuralNetTest {


    public static boolean testInit(){
        NeuralNetParams params = new NeuralNetParams();

        Random r = new Random();

        for (int i = 0; i < 10000; i++) {
            boolean bias = r.nextBoolean();
            int inputs = r.nextInt(100)+1;
            int hiddenLayers = r.nextInt(20)+1;
            int[] neuronsInHiddenLayers = new int[hiddenLayers];
            for(int n =0 ;n < hiddenLayers; n++)
                neuronsInHiddenLayers[n] = r.nextInt(20)+1;

            int outputs = r.nextInt(100)+1;


            params.setBiasNeuron(bias);
            params.setInputNeurons(inputs);
            params.setNumberOfHiddenLayers(hiddenLayers);
            params.setHiddenNeuronsInLayer(neuronsInHiddenLayers);
            params.setOutputNeurons(outputs);

            NeuralNet nn = new NeuralNet(params);
            nn.init();

            if(nn.getInputNeurons().size() != inputs)
                return false;
            if(nn.getHiddenLayers().size() != hiddenLayers)
                return false;

            for (int j = 0; j < nn.getHiddenLayers().size(); j++) {
                List<Neuron> hiddenLayer = nn.getHiddenLayers().get(j);

                if(hiddenLayer.size() != neuronsInHiddenLayers[j])
                    return false;
            }

            if(nn.getOutputNeurons().size() != outputs)
                return false;

            if((nn.getBiasNeuron() != null) && !bias)
                return false;

            if((nn.getBiasNeuron() == null) && bias)
                return false;

        }

        return true;
    }



    public static boolean testFeedForward(){
        NeuralNetParams params = new NeuralNetParams();
        params.setInputNeurons(1);
        params.setHiddenNeuronsInLayer(1);
        params.setOutputNeurons(1);
        params.setBiasNeuron(false);
        params.setNumberOfHiddenLayers(1);
        params.setSigmoid(Double::doubleValue);
        NeuralNet NN = new NeuralNet(params);
        NN.init();
        List<Neuron> neurons = NN.getHiddenLayers().get(0);

        Random r = new Random();

        for (int i = 0; i < 10000; i++) {
            double jiEdgeWeight = r.nextDouble();
            double kjEdgeWeight = r.nextDouble();
            neurons.get(0).getInputEdges().forEach(edge -> edge.setWeight(jiEdgeWeight));
            neurons.get(0).getOutputEdges().forEach(edge -> edge.setWeight(kjEdgeWeight));

            double input = r.nextDouble();
            List<Double> output = NN.feedForward(Arrays.asList(input));

            if( Math.abs(output.get(0) - (input*jiEdgeWeight*kjEdgeWeight)) > Double.MIN_VALUE )
                return false;
        }


        return true;
    }
}