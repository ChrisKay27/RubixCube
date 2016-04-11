package neuralnet;

import genetics.DoubleGene;
import genetics.Gene;
import genetics.Genome;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 4/8/2016.
 */
public class NNToGenomeConverter {

    public static Genome getGenome(NeuralNet nn) {

        Genome g = new Genome();

        List<Gene> genes = new ArrayList<>();


        nn.getHiddenLayers().forEach(hiddenLayer -> hiddenLayer.forEach(neuron -> neuron.getInputEdges().forEach(edge -> genes.add(new DoubleGene(edge.getWeight())))));
        nn.getOutputNeurons().forEach(neuron -> neuron.getInputEdges().forEach(edge -> genes.add(new DoubleGene(edge.getWeight()))));

        g.setUserData(nn.getNNParams());
        g.setGenes(genes);
        return g;
    }

    public static NeuralNet getNeuralNet(Genome g) {

        NeuralNetParams params = (NeuralNetParams) g.getUserData();

        NeuralNet nn = new NeuralNet(params);
        nn.init();
        
        List<Gene> genes = new LinkedList<>(g.getGenes());
        nn.getHiddenLayers().forEach(hiddenLayer -> hiddenLayer.forEach(neuron -> neuron.getInputEdges().forEach(edge -> edge.setWeight(((DoubleGene)genes.remove(0)).getValue()))));
        nn.getOutputNeurons().forEach(neuron -> neuron.getInputEdges().forEach(edge -> edge.setWeight(((DoubleGene)genes.remove(0)).getValue())));

        return nn;
    }

    public static NeuralNet getNeuralNet(Genome g, NeuralNet nn) {

        List<Gene> genes = new LinkedList<>(g.getGenes());
        nn.getHiddenLayers().forEach(hiddenLayer -> hiddenLayer.forEach(neuron -> neuron.getInputEdges().forEach(edge -> edge.setWeight(((DoubleGene)genes.remove(0)).getValue()))));
        nn.getOutputNeurons().forEach(neuron -> neuron.getInputEdges().forEach(edge -> edge.setWeight(((DoubleGene)genes.remove(0)).getValue())));

        return nn;
    }
}
