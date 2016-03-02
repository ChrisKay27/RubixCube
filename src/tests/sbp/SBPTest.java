package tests.sbp;


import neuralnet.Edge;
import neuralnet.NeuralNet;
import neuralnet.Neuron;
import sbp.SBP;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by chris_000 on 2/23/2016.
 */
public class SBPTest {

//    private static Function<Double,Double> deriv_sigmoid = net -> A*(1-Math.pow(Math.tanh(B*net),2));


    public static boolean testDeltaK()  {

        Random r = new Random();

        for (int i = 0; i < 10000; i++) {

            double A = r.nextDouble();
            double B = r.nextDouble();
            double T = r.nextDouble();
            double Z = r.nextDouble();
            double net = r.nextDouble();

            double deltaK = (T-Z)*A*(1-Math.pow(Math.tanh(B*net),2));

            double actualOutput = SBP.deltaK(T,Z,net,Net -> A*(1-Math.pow(Math.tanh(B*Net),2)));
            if( Math.abs(deltaK - actualOutput) > 0.00000000001)
                return false;

        }

        return true;
    }

    
    public static boolean testDeltaKj()  {

        Random r = new Random();

        for (int i = 0; i < 10000; i++) {
            double deltaK = r.nextDouble();
            double actJ = r.nextDouble();
            double N = r.nextDouble();

            double deltaKj = N * deltaK * actJ;

            if (deltaKj != SBP.deltaKj(deltaK, actJ, N))
                return false;
        }
        return true;
    }

    
    public static boolean testDeltaJ()  {
        Map<Neuron,Double> deltas = new HashMap<>();

        Neuron hidden = new Neuron("h1");
        Neuron output = new Neuron("o0");
        Neuron output2 = new Neuron("o2");

        Random r = new Random();

        hidden.getOutputEdges().add(new Edge(r.nextDouble(),hidden,output));
        hidden.getOutputEdges().add(new Edge(r.nextDouble(),hidden,output2));

        for (int i = 0; i < 10000; i++) {
            double A = r.nextDouble();
            double B = r.nextDouble();

            hidden.getOutputEdges().forEach(edge -> edge.setWeight(r.nextDouble()));
            deltas.put(output,r.nextDouble());
            deltas.put(output2,r.nextDouble());


            hidden.setNet(r.nextDouble());
            double net = hidden.getNet();

            double t = A * (1 - Math.pow(Math.tanh(B * net), 2));

            double sumOfWeightMultErrors = 0;
            for (Edge e : hidden.getOutputEdges()) {
                sumOfWeightMultErrors += e.getWeight() * deltas.get(e.getDest());
            }

            if( t * sumOfWeightMultErrors != SBP.deltaJ(hidden, deltas, Net -> A*(1-Math.pow(Math.tanh(B*Net),2))))
                return false;
        }

        return true;
    }

    
    public static boolean testDeltaJi()  {
        Random r = new Random();

        for (int i = 0; i < 10000; i++) {
            double N = r.nextDouble();
            double actI = r.nextDouble();
            double deltaJ = r.nextDouble();

            if( N*actI*deltaJ != SBP.deltaJi(deltaJ,actI,N))
                return false;
        }

        return true;
    }
}