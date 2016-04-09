package ui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import neuralnet.Edge;
import neuralnet.NeuralNet;
import neuralnet.Neuron;
import sbp.SBPNNExperiment;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 4/9/2016.
 */
public class NNPanel {


    private final mxGraphComponent graphComponent;
    private long sleepTime;
    private Runnable updateUIRunnable;
    private NeuralNet nn;
    private boolean drawEdges = true;


    public NNPanel(NeuralNet nn) {
        this.nn = nn;
        //Center Panel
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        List<Object> graphObjects = new ArrayList<>();

        updateUIRunnable = () -> {
            //System.out.println("updating graph");

            // System.out.println("actually updating graph");
            final NeuralNet neuralNet = new NeuralNet(this.nn);
            SwingUtilities.invokeLater(() -> {
                graph.getModel().beginUpdate();
                try {
                    graphObjects.forEach(o -> graph.getModel().remove(o));
                    graphObjects.clear();

                    Map<Neuron, Object> neurGraph = new HashMap<>();

                    int i = 0;
                    for (Neuron n : neuralNet.getInputNeurons()) {
                        Object graphObject = graph.insertVertex(parent, "i" + i++, n, 300, 200 + i * 80, 80, 30);
                        graphObjects.add(graphObject);
                        neurGraph.put(n, graphObject);
                    }
                    i = 0;

                    final List<List<Neuron>> hiddenLayers = neuralNet.getHiddenLayers();
                    int j = 0;
                    for (List<Neuron> hiddenNeurons : hiddenLayers) {
                        for (Neuron n : hiddenNeurons) {
                            Object graphObject = graph.insertVertex(parent, "h" + i++, n, (j * 100) + 600, 200 + i * 80, 80, 30);
                            graphObjects.add(graphObject);
                            neurGraph.put(n, graphObject);
                        }
                        j++;
                        i = 0;
                    }

                    i = 0;
                    for (Neuron n : neuralNet.getOutputNeurons()) {
                        Object graphObject = graph.insertVertex(parent, "o" + i++, n, (j * 100) + 900, 200 + i * 80, 80, 30);
                        graphObjects.add(graphObject);
                        neurGraph.put(n, graphObject);
                    }

                    Object graphObject = graph.insertVertex(parent, "o" + i++, neuralNet.getBiasNeuron(), 500, 100, 80, 30);
                    graphObjects.add(graphObject);
                    neurGraph.put(neuralNet.getBiasNeuron(), graphObject);


                    if (drawEdges)
                        for (Neuron n : neurGraph.keySet()) {
                            if (n.getOutputEdges() != null)
                                for (Edge e : n.getOutputEdges()) {
                                    graphObject = graph.insertEdge(parent, null, e, neurGraph.get(e.getSource()), neurGraph.get(e.getDest()));
                                    graphObjects.add(graphObject);
                                }
                        }


                    graph.refresh();
                    graph.repaint();
//                    repaint();


                } finally {
                    graph.getModel().endUpdate();
                }
            });

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("done updating graph");
        };
        updateUIRunnable.run();
        graphComponent = new mxGraphComponent(graph);

    }


    public void update(){
        updateUIRunnable.run();
    }

    public void setDrawEdges(boolean b){
        drawEdges = b;
    }


    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setNn(NeuralNet nn) {
        this.nn = nn;
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public List<Double> feedForward(List<Double> inputs){
        return nn.feedForward(inputs);
    }
}
