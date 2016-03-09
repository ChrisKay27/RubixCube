package neuralnet;

public class NNExperimentParams {
    private double a;
    private double b;
    private double n;
    private boolean usingBias;
    private int epochs;
    private int trainingIterationsPerEpoch;
    private int inputs;
    private int hiddenLayers;
    private double desiredErrorRate;
    private int[] hiddenNeuronsPerLayer;
    private double alpha;
    private double weightDecay;


    public NNExperimentParams(double a, double b, double n, boolean usingBias, int epochs, int trainingIterationsPerEpoch, int inputs, int hiddenLayers, double desiredErrorRate, double alpha, int... hiddenNeuronsPerLayer) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.usingBias = usingBias;
        this.epochs = epochs;
        this.trainingIterationsPerEpoch = trainingIterationsPerEpoch;
        this.inputs = inputs;
        this.hiddenLayers = hiddenLayers;
        this.desiredErrorRate = desiredErrorRate;
        this.alpha = alpha;
        this.hiddenNeuronsPerLayer = hiddenNeuronsPerLayer;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getN() {
        return n;
    }

    public boolean isUsingBias() {
        return usingBias;
    }

    public int getHiddenLayers() {
        return hiddenLayers;
    }

    public double getDesiredErrorRate() {
        return desiredErrorRate;
    }

    public int[] getHiddenNeuronsPerLayer() {
        return hiddenNeuronsPerLayer;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getTrainingIterationsPerEpoch() {
        return trainingIterationsPerEpoch;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setN(double n) {
        this.n = n;
    }

    public void setUsingBias(boolean usingBias) {
        this.usingBias = usingBias;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void setTrainingIterationsPerEpoch(int trainingIterationsPerEpoch) {
        this.trainingIterationsPerEpoch = trainingIterationsPerEpoch;
    }

    public int getInputs() {
        return inputs;
    }

    public void setInputs(int inputs) {
        this.inputs = inputs;
    }

    public void setHiddenLayers(int hiddenLayers) {
        this.hiddenLayers = hiddenLayers;
    }

    public void setDesiredErrorRate(double desiredErrorRate) {
        this.desiredErrorRate = desiredErrorRate;
    }

    public void setHiddenNeuronsPerLayer(int[] hiddenNeuronsPerLayer) {
        this.hiddenNeuronsPerLayer = hiddenNeuronsPerLayer;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getWeightDecay() {
        return weightDecay;
    }

    public void setWeightDecay(double weightDecay) {
        this.weightDecay = weightDecay;
    }
}
