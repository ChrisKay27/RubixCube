package neuralnet;

import java.util.function.Function;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class SBPParams {
    private double N;
    private double Yj;
    private Function<Double,Double> deriv_sigmoid;

    public double getN() {
        return N;
    }

    public void setN(double n) {
        N = n;
    }

    public double getYj() {
        return Yj;
    }

    public void setYj(double yj) {
        Yj = yj;
    }

    public Function<Double, Double> getDeriv_sigmoid() {
        return deriv_sigmoid;
    }

    public void setDeriv_sigmoid(Function<Double, Double> deriv_sigmoid) {
        this.deriv_sigmoid = deriv_sigmoid;
    }
}
