package neuralnet;

import java.io.Serializable;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class Edge implements Serializable {
    private double weight;
    private final Neuron source;
    private final Neuron dest;
    private double prevDeltaWeight;
    private double deltaWeight;

    public Edge(double weight, Neuron left, Neuron right) {
        this.weight = weight;
        this.source = left;
        this.dest = right;
    }



    public double getWeight() {
        return weight;
    }

    public double getDeltaWeight() {
        return deltaWeight;
    }

    public void setDeltaWeight(double deltaWeight) {
        this.deltaWeight = deltaWeight;
    }

    public void applyDeltaWeight(double alpha){
        weight = weight + (1-alpha)*deltaWeight + alpha*prevDeltaWeight;
        prevDeltaWeight = deltaWeight;
    }

    public Neuron getSource() {
        return source;
    }

    public Neuron getDest() {
        return dest;
    }

    @Override
    public String toString() {
        return "" + (float)weight;
    }

    public void set(Edge e) {
        weight = e.weight;
        deltaWeight = e.deltaWeight;
    }

    public double getPrevDeltaWeight() {
        return prevDeltaWeight;
    }

    public void setPrevDeltaWeight(double prevDeltaWeight) {
        this.prevDeltaWeight = prevDeltaWeight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
