package tests.neuralnet;


import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import neuralnet.Neuron;

import java.util.List;
import java.util.Random;

/**
 * Created by chris_000 on 2/23/2016.
 */
public class NeuralNetTest {


    public boolean testInit(){
        NeuralNetParams params = new NeuralNetParams();

        Random r = new Random();

        for (int i = 0; i < 10000; i++) {
            boolean bias = r.nextBoolean();
            int inputs = r.nextInt(100);
            int hiddenLayers = r.nextInt(20);
            int[] neuronsInHiddenLayers = new int[hiddenLayers];
            for(int n =0 ;n < hiddenLayers; n++)
                neuronsInHiddenLayers[n] = r.nextInt(20);

            int outputs = r.nextInt(100);


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
                List<Neuron> hiddenLayer = nn.getHiddenLayers().get(i);

                if(hiddenLayer.size() != neuronsInHiddenLayers[i])
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



    public boolean testFeedForward(){
        return false;
    }
}