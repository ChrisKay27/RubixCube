package neuralnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Chris on 2/9/2016.
 */
public class Neuron implements Serializable {
    private final List<Edge> inputEdges = new ArrayList<>();
    private final List<Edge> outputEdges = new ArrayList<>();

    private double net;
    private double act;
    private String name;

    public Neuron(String name) {
        this.name = name;
    }


    public List<Edge> getInputEdges() {
        return inputEdges;
    }

    Edge getBiasInputEdge() {
        return inputEdges.get(inputEdges.size()-1);
    }

    public List<Edge> getOutputEdges() {
        return outputEdges;
    }


    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getAct() {
        return act;
    }

    public void setAct(double act) {
        this.act = act;
    }

    @Override
    public String toString() {
        return name;
    }
}
