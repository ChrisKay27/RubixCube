package neuralnet;

import sbp.SBPImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2/9/2016.
 */
public class NeuralNet implements SBPImpl, Serializable {

    private final List<Neuron> inputNeurons = new ArrayList<>();
    private final List<List<Neuron>> hiddenLayers = new ArrayList<>();
    private final List<Neuron> outputNeurons = new ArrayList<>();
    private Neuron bias;

    private final NeuralNetParams params;
    private double networkError;

    public NeuralNet(NeuralNetParams params) {
        this.params = params;
    }

    /**
     * Copy constructor for graphical output...
     */
    public NeuralNet(NeuralNet neuralNet) {
        params = neuralNet.params;

        init();


        for (int hiddenLayerIndex = 0; hiddenLayerIndex < params.getNumberOfHiddenLayers(); hiddenLayerIndex++) {
            List<Neuron> hiddenNeurons = hiddenLayers.get(hiddenLayerIndex);

            for (int i = 0; i < hiddenNeurons.size(); i++) {
                Neuron hiddenNeuron = hiddenNeurons.get(i);

                for (int j = 0; j < hiddenNeuron.getInputEdges().size(); j++)
                    hiddenNeuron.getInputEdges().get(j).set(neuralNet.hiddenLayers.get(hiddenLayerIndex).get(i).getInputEdges().get(j));

                for (int j = 0; j < hiddenNeuron.getOutputEdges().size(); j++)
                    hiddenNeuron.getOutputEdges().get(j).set(neuralNet.hiddenLayers.get(hiddenLayerIndex).get(i).getOutputEdges().get(j));

            }
        }

        for (int j = 0; j < bias.getOutputEdges().size(); j++)
            bias.getOutputEdges().get(j).set(neuralNet.bias.getOutputEdges().get(j));
    }


    public void init(){
        if(params.usingBiasNeuron()) {
            bias = new Neuron("Bias");
            bias.setAct(1);
            bias.setNet(1);
        }

        inputNeurons.clear();

        hiddenLayers.clear();

        outputNeurons.clear();



        for (int i = 0; i < params.getInputNeurons(); i++)
            inputNeurons.add(new Neuron("i"+i));

        for (int hiddenLayerIndex = 0; hiddenLayerIndex < params.getNumberOfHiddenLayers(); hiddenLayerIndex++) {
            List<Neuron> hiddenNeurons = new ArrayList<>();
            for (int i = 0; i < params.getHiddenNeuronsInLayer()[hiddenLayerIndex]; i++)
                hiddenNeurons.add(new Neuron("h"+hiddenLayerIndex+""+i));
            hiddenLayers.add(hiddenNeurons);
        }

        for (int i = 0; i < params.getOutputNeurons(); i++)
            outputNeurons.add(new Neuron("o"+i));



        inputNeurons.forEach(neuron -> {
            addEdges(neuron,hiddenLayers.get(0));});

        for (int i = 0; i < hiddenLayers.size()-1; i++) {
            List<Neuron> hiddenNeurons = hiddenLayers.get(i);
            int nextLayerIndex = i+1;
            hiddenNeurons.forEach(neuron -> {
                addEdges(neuron,hiddenLayers.get(nextLayerIndex));
            });
        }

        hiddenLayers.get(hiddenLayers.size()-1).forEach(neuron -> {
            addEdges(neuron,outputNeurons);
        });

        if (params.usingBiasNeuron()) {
            hiddenLayers.forEach(hiddenNeurons -> addEdges(bias,hiddenNeurons));
            addEdges(bias,outputNeurons);
        }
       // System.out.println("Done init net!");
    }

    private static void addEdges(Neuron source, List<Neuron> dests){
        for (Neuron dest : dests) {
            Edge e = new Edge(-6 + Math.random() * 12, source, dest);
            source.getOutputEdges().add(e);
            dest.getInputEdges().add(e);
        }
    }

    public List<Double> feedForward(List<Double> input){
        //hidden layers

        hiddenLayers.forEach(hiddenNeurons -> {
                    for (int h = 0; h < hiddenNeurons.size(); h++) {
                        Neuron hiddenNeuron = hiddenNeurons.get(h);
                        List<Edge> hiddenInputEdges = hiddenNeuron.getInputEdges();

                        double net = 0;
                        for (int i = 0; i < input.size(); i++)
                            net += input.get(i) * hiddenInputEdges.get(i).getWeight();
                        if (params.usingBiasNeuron())
                            net += hiddenInputEdges.get(input.size()).getWeight() * bias.getNet();
                        hiddenNeuron.setNet(net);
                        hiddenNeuron.setAct(params.getSigmoid().apply(net));
                    }
                });

        List<Double> outputs = new ArrayList<>();


        //output layer
        for (int o = 0; o < outputNeurons.size(); o++) {
            Neuron outputNeuron = outputNeurons.get(o);
            List<Edge> outputsInputEdges = outputNeuron.getInputEdges();
            List<Neuron> hiddenNeurons = hiddenLayers.get(hiddenLayers.size()-1);

            double net = 0;
            for (int i = 0; i < hiddenNeurons.size(); i++)
                net += hiddenNeurons.get(i).getAct()*outputsInputEdges.get(i).getWeight();
            if( params.usingBiasNeuron() )
                net += outputsInputEdges.get(hiddenNeurons.size()).getWeight()*bias.getNet();
            outputNeuron.setNet(net);

            double act = params.getSigmoid().apply(net);
            outputNeuron.setAct(act);
            outputs.add(act);
        }

        //input layer
        for (int i = 0; i < inputNeurons.size(); i++) {
            Neuron inputNeuron = inputNeurons.get(i);
            inputNeuron.setAct(input.get(i));
            inputNeuron.setNet(input.get(i));
        }

        return outputs;
    }


    public List<Neuron> getOutputNeurons() {
        return outputNeurons;
    }

    public List<List<Neuron>> getHiddenLayers() {
        return hiddenLayers;
    }



    public List<Neuron> getInputNeurons() {
        return inputNeurons;
    }

    public Neuron getBiasNeuron() {
        return bias;
    }

    public double getNetworkError() {
        return networkError;
    }

    public void setNetworkError(double networkError) {
        this.networkError = networkError;
    }

    public NeuralNetParams getNNParams() {
        return params;
    }
}
