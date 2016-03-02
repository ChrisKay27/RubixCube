package ui;

import NNRubixCube.NNRubixCube;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import neuralnet.*;
import sbp.SBP.SBPResults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  *
 * Created by Chris on 2/9/2016.
 */
public class NeuralNetPanel extends JPanel {

    private boolean updateUI = true;
    private int sleepTime = 100;
    private NNRubixCube xorProblem = new NNRubixCube(new NNExperimentParams(1.716, 0.667, 0.125, true, 100, 1000, 324 ,1, 0.000001 , 0.01, 5));

    private Runnable updateUIRunnable;



    public NeuralNetPanel() {
        super(new BorderLayout());
        xorProblem.init();

        JPanel northPanel = new JPanel(new GridLayout(2,10));


        JPanel temp = new JPanel();
        JLabel ALabel = new JLabel("A:");
        JTextField ATextBox = new JTextField("1.716",10);
        ALabel.setLabelFor(ATextBox);
        temp.add(ALabel);
        temp.add(ATextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel BLabel = new JLabel("B:");
        JTextField BTextBox = new JTextField("0.667",10);
        BLabel.setLabelFor(BTextBox);
        temp.add(BLabel);
        temp.add(BTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel NLabel = new JLabel("Learning Rate:");
        JTextField NTextBox = new JTextField("0.125",10);
        NLabel.setLabelFor(NTextBox);
        temp.add(NLabel);
        temp.add(NTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel alphaLabel = new JLabel("Momentum Alpha:");
        JTextField AlphaTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(AlphaTextBox);
        temp.add(alphaLabel);
        temp.add(AlphaTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel TrainingItsLabel = new JLabel("Training Iterations:");
        JTextField TrainingItsTextBox = new JTextField("1000",10);
        TrainingItsLabel.setLabelFor(TrainingItsTextBox);
        temp.add(TrainingItsLabel);
        temp.add(TrainingItsTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel EpochLabel = new JLabel("Training Epochs:");
        JTextField EpochTextBox = new JTextField("100",6);
        EpochLabel.setLabelFor(EpochTextBox);
        temp.add(EpochLabel);
        temp.add(EpochTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel HiddenLayersLabel = new JLabel("Hidden Layers:");
        JTextField HiddenLayersTextBox = new JTextField("1",10);
        HiddenLayersLabel.setLabelFor(HiddenLayersTextBox);
        temp.add(HiddenLayersLabel);
        temp.add(HiddenLayersTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel HidNeuronsPerLayerLabel = new JLabel("Hidden Neurons Per Layer ('2-3' if you want a 2-2-3-1 NN):");
        JTextField HidNeuronsPerLayerTextBox = new JTextField("2",10);
        HidNeuronsPerLayerLabel.setLabelFor(HidNeuronsPerLayerTextBox);
        temp.add(HidNeuronsPerLayerLabel);
        temp.add(HidNeuronsPerLayerTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        JLabel ErrorRateLabel = new JLabel("Error Rate:");
        JTextField ErrorRateTextBox = new JTextField("0.000001",20);
        ErrorRateLabel.setLabelFor(ErrorRateTextBox);
        temp.add(ErrorRateLabel);
        temp.add(ErrorRateTextBox);
        northPanel.add(temp);

        temp = new JPanel();
        final JButton createNNButton = new JButton("Create NN");
        createNNButton.addActionListener(e -> {
            double A = Double.parseDouble(ATextBox.getText());
            double B = Double.parseDouble(BTextBox.getText());
            double N = Double.parseDouble(NTextBox.getText());
            int hiddenLayers = Integer.parseInt(HiddenLayersTextBox.getText());
            int trainingIterationsPerEpoch = Integer.parseInt(TrainingItsTextBox.getText());
            int epochs = Integer.parseInt(EpochTextBox.getText());
            double desiredErrorRate = Double.parseDouble(ErrorRateTextBox.getText());
            double alpha = Double.parseDouble(AlphaTextBox.getText());


            String[] hNeuronsSpl = HidNeuronsPerLayerTextBox.getText().split("-");
            int[] hNeurons = new int[hNeuronsSpl.length];
            for (int i = 0; i < hNeuronsSpl.length; i++) {
                String num = hNeuronsSpl[i];
                hNeurons[i] = Integer.parseInt(num);
            }

            xorProblem = new NNRubixCube(new NNExperimentParams(A, B, N, true, epochs, trainingIterationsPerEpoch, 324, hiddenLayers, desiredErrorRate, alpha, hNeurons));
            xorProblem.setUpdateListener(updateUIRunnable);
            xorProblem.init();

            boolean updateUITemp = updateUI;
            updateUI = true;
            updateUIRunnable.run();
            updateUI = updateUITemp;
        });
        temp.add(createNNButton);
        northPanel.add(temp);

        temp = new JPanel();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        final JButton loadNNButton = new JButton("Load NN");
        loadNNButton.addActionListener(e -> {

            int returnVal = fc.showOpenDialog(NeuralNetPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                xorProblem.setNeuralNet(NeuralNetIO.loadNN(file));

                boolean updateUITemp = updateUI;
                updateUI = true;
                updateUIRunnable.run();
                updateUI = updateUITemp;
            }
        });
        temp.add(loadNNButton);

        final JButton saveNNButton = new JButton("Save NN");
        saveNNButton.addActionListener(e -> {

            int returnVal = fc.showSaveDialog(NeuralNetPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                NeuralNetIO.saveNN(xorProblem.getNeuralNet(), file);
            }
        });
        temp.add(saveNNButton);
        northPanel.add(temp);


        JLabel warningLabel = new JLabel("<html>You must hit 'create NN' before pressing<br> 'run' to create a NN with these parameters.</html>");
        northPanel.add(warningLabel);

        add(northPanel,BorderLayout.NORTH);


        //Center Panel
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        List<Object> graphObjects = new ArrayList<>();


        updateUIRunnable = () -> {
                //System.out.println("updating graph");
                if(!updateUI) return;
               // System.out.println("actually updating graph");
                final NeuralNet neuralNet = new NeuralNet(xorProblem.getNeuralNet());
                SwingUtilities.invokeLater(()->{
                    graph.getModel().beginUpdate();
                    try {
                        graphObjects.forEach(o->graph.getModel().remove(o));
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
                        int j=0;
                        for (List<Neuron> hiddenNeurons : hiddenLayers ) {
                            for (Neuron n : hiddenNeurons) {
                                Object graphObject = graph.insertVertex(parent, "h" + i++, n, (j * 100) + 600, 200 + i * 80, 80, 30);
                                graphObjects.add(graphObject);
                                neurGraph.put(n, graphObject);
                            }
                            j++;
                            i=0;
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


                        for (Neuron n : neurGraph.keySet()) {
                            if (n.getOutputEdges() != null)
                                for (Edge e : n.getOutputEdges()) {
                                    graphObject = graph.insertEdge(parent, null, e, neurGraph.get(e.getSource()), neurGraph.get(e.getDest()));
                                    graphObjects.add(graphObject);
                                }
                        }


                        graph.refresh();
                        graph.repaint();
                        repaint();


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
        xorProblem.setUpdateListener(updateUIRunnable);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        add(graphComponent,BorderLayout.CENTER);



        JPanel southPanel = new JPanel();

        JLabel TTLabel = new JLabel("Training Tuple:");
        JTextField TTField = new JTextField("-1,-1",20);
        TTLabel.setLabelFor(TTField);
        southPanel.add(TTField);
        southPanel.add(TTLabel);

        JLabel OutputLabel = new JLabel("Output: ");
        southPanel.add(OutputLabel);

        JLabel FILLER = new JLabel("      ");
        southPanel.add(FILLER);

        final JButton FFButton = new JButton("Feed It Forward");
        FFButton.addActionListener(e -> {
            List<Double> inputs = new ArrayList<>();
            String[] ttSplit = TTField.getText().split(",");
            for(String input : ttSplit)
                inputs.add(Double.parseDouble(input));

            List<Double> outputs = xorProblem.getNeuralNet().feedForward(inputs);

            StringBuilder sb = new StringBuilder();
            outputs.forEach(output -> sb.append(String.format("%1.0f,",output)));
            OutputLabel.setText("Output: " + sb.substring(0,sb.length()-1));
        });
        southPanel.add(FFButton);

        FILLER = new JLabel("          ");
        southPanel.add(FILLER);


        JCheckBox updateUICheckbox = new JCheckBox("Update UI");


        final JButton run = new JButton("Run (The NN above)");
        run.addActionListener(e -> new Thread(()->{
            run.setEnabled(false);
            System.out.println("Running Experiment!");
            final SBPResults runResults = xorProblem.run();

            double error = runResults.networkError;
            System.out.println("Experiment over!");
            System.out.println("Iterations: " + runResults.numberOfIterationsTaken + " Epochs: " + runResults.numberOfEpochs + " Error: " + error);

            updateUI = true;
            updateUIRunnable.run();

            updateUICheckbox.setSelected(true);

            run.setEnabled(true);

        }).start());
        southPanel.add(run);

        JLabel delayLabel = new JLabel("Delay:");
        southPanel.add(delayLabel);
        JTextField sleepTimeField = new JTextField("100",10);
        Runnable updateSleepBetweenStatesTime = () -> {
            try {
                sleepTime = Integer.parseInt(sleepTimeField.getText());
            } catch (Exception ex) {
                sleepTimeField.setText("0");
                sleepTime = 0;
            }
        };
        sleepTimeField.addActionListener(e -> updateSleepBetweenStatesTime.run());
        sleepTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateSleepBetweenStatesTime.run();
            }
        });
        southPanel.add(sleepTimeField);


        updateUICheckbox.addActionListener(e->{
            updateUI = updateUICheckbox.isSelected();
            updateUIRunnable.run();
        });
        updateUICheckbox.setSelected(true);
        southPanel.add(updateUICheckbox);

        JLabel infoLabel = new JLabel("Note: You have to press 'Create NN' to create a new NN with the above parameters");
        southPanel.add(infoLabel);

        add(southPanel, BorderLayout.SOUTH);


    }
}
