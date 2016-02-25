package neuralnet;

public class NNExperimentParams {
    private final double a;
    private final double b;
    private final double n;
    private final boolean usingBias;
    private final int epochs;
    private final int trainingIterationsPerEpoch;
    private final int hiddenLayers;
    private final double desiredErrorRate;
    private final int[] hiddenNeuronsPerLayer;
    private final double alpha;


    public NNExperimentParams(double a, double b, double n, boolean usingBias, int epochs, int trainingIterationsPerEpoch, int hiddenLayers, double desiredErrorRate, double alpha, int... hiddenNeuronsPerLayer) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.usingBias = usingBias;
        this.epochs = epochs;
        this.trainingIterationsPerEpoch = trainingIterationsPerEpoch;
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

}
