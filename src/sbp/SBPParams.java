package sbp;

import java.util.function.Function;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class SBPParams {

    private double N;
    private Function<Double,Double> deriv_sigmoid;
    private Runnable SBPListener;
    private double desiredErrorRate;
    private double alpha;
    private int epocs;
    private int trainingIterations;


    public double getN() {
        return N;
    }

    public void setN(double n) {
        N = n;
    }


    public Function<Double, Double> getDeriv_sigmoid() {
        return deriv_sigmoid;
    }

    public void setDeriv_sigmoid(Function<Double, Double> deriv_sigmoid) {
        this.deriv_sigmoid = deriv_sigmoid;
    }


    public Runnable getSBPListener() {
        return SBPListener;
    }

    public void setSBPListener(Runnable SBPListener) {
        this.SBPListener = SBPListener;
    }

    public double getDesiredErrorRate() {
        return desiredErrorRate;
    }

    public void setDesiredErrorRate(double desiredErrorRate) {
        this.desiredErrorRate = desiredErrorRate;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public int getEpocs() {
        return epocs;
    }

    public void setEpocs(int epocs) {
        this.epocs = epocs;
    }

    public int getTrainingIterations() {
        return trainingIterations;
    }

    public void setTrainingIterations(int trainingIterations) {
        this.trainingIterations = trainingIterations;
    }
}
