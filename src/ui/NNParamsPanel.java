package ui;

import neuralnet.*;
import org.jetbrains.annotations.Nullable;
import sbp.SBP;
import sbp.SBPNNExperiment;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 2/24/2016.
 */
public class NNParamsPanel extends JPanel{
    private final JTextField ATextBox;
    private final JTextField BTextBox;
    private final JTextField ErrorRateTextBox;
    private final JTextField NTextBox;
    private final JTextField AlphaTextBox;
    private final JTextField TrainingItsTextBox;
    private final JTextField HidNeuronsPerLayerTextBox;
    private final JTextField EpochTextBox;
    private final JTextField weightDecayTextBox;
    private final JTextField inputsTextBox;
    private final JTextField outputsTextBox;
    private boolean useBias = true;
    private List<TrainingTuple> trainingTuples;
    private boolean updateLabel = true;

    private @Nullable NeuralNet nnToUse;

    private SBPNNExperiment sbpNNExperiment;

    public NNParamsPanel() {
        super(new GridLayout(2,11));

        setBorder(BorderFactory.createTitledBorder("Neural Network Panel"));

        JPanel temp = new JPanel();
        JPanel temp2 = new JPanel();
        JLabel ALabel = new JLabel("A:");
        ATextBox = new JTextField("1.716",10);
        ALabel.setLabelFor(ATextBox);
        temp2.add(ALabel);
        temp2.add(ATextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel BLabel = new JLabel("B:");
        BTextBox = new JTextField("0.667",10);
        BLabel.setLabelFor(BTextBox);
        temp2.add(BLabel);
        temp2.add(BTextBox);
        temp.add(temp2);
        add(temp);


        temp = new JPanel();
        temp2 = new JPanel();
        JLabel NLabel = new JLabel("Learning Rate:");
        NTextBox = new JTextField("0.125",10);
        NLabel.setLabelFor(NTextBox);
        temp2.add(NLabel);
        temp2.add(NTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel alphaLabel = new JLabel("Momentum Alpha:");
        AlphaTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(AlphaTextBox);
        temp2.add(alphaLabel);
        temp2.add(AlphaTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel weightDecayLabel = new JLabel("Weight Decay:");
        weightDecayTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(weightDecayTextBox);
        temp2.add(weightDecayLabel);
        temp2.add(weightDecayTextBox);
        temp.add(temp2);
        add(temp);

        temp = new JPanel();
        temp2 = new JPanel();
        JLabel TrainingItsLabel = new JLabel("Training Iterations:");
        TrainingItsTextBox = new JTextField("1000",10);
        TrainingItsLabel.setLabelFor(TrainingItsTextBox);
        temp2.add(TrainingItsLabel);
        temp2.add(TrainingItsTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel EpochLabel = new JLabel("Training Epochs:");
        EpochTextBox = new JTextField("100",6);
        EpochLabel.setLabelFor(EpochTextBox);
        temp2.add(EpochLabel);
        temp2.add(EpochTextBox);
        temp.add(temp2);
        add(temp);

        temp = new JPanel();
        temp2 = new JPanel();
        JCheckBox biasCheckbox = new JCheckBox("Use Bias Neuron");
        biasCheckbox.setSelected(true);
        biasCheckbox.addActionListener(e2 -> useBias = !useBias);
        temp2.add(biasCheckbox);
        temp.add(temp2);


        temp2 = new JPanel();
        JLabel inputsLabel = new JLabel("Inputs: ");
        inputsTextBox = new JTextField("324",10);
        inputsLabel.setLabelFor(inputsTextBox);
        temp2.add(inputsLabel);
        temp2.add(inputsTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel HidNeuronsPerLayerLabel = new JLabel("<html>Hidden Neurons Per Layer<br> ('2-3' if you want a 2-2-3-1 NN):</html>");
        HidNeuronsPerLayerTextBox = new JTextField("2",20);
        HidNeuronsPerLayerLabel.setLabelFor(HidNeuronsPerLayerTextBox);
        temp2.add(HidNeuronsPerLayerLabel);
        temp2.add(HidNeuronsPerLayerTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel outputsLabel = new JLabel("Outputs: ");
        outputsTextBox = new JTextField("7",10);
        outputsLabel.setLabelFor(outputsTextBox);
        temp2.add(outputsLabel);
        temp2.add(outputsTextBox);
        temp.add(temp2);
        add(temp);


        temp2 = new JPanel();
        temp = new JPanel();
        JLabel ErrorRateLabel = new JLabel("Error Rate:");
        ErrorRateTextBox = new JTextField("0.000001",20);
        ErrorRateLabel.setLabelFor(ErrorRateTextBox);
        temp2.add(ErrorRateLabel);
        temp2.add(ErrorRateTextBox);
        temp.add(temp2);
        add(temp);

        final JButton loadNNButton = new JButton("Load NN");
        final JButton saveNNButton = new JButton("Save NN");

        JLabel infoLabel = new JLabel("Status: Not started");



        temp = new JPanel();
        final JButton stop = new JButton("Stop");
        final JButton run = new JButton("Run");
        final JButton clear = new JButton("Clear Loaded NN");

        JLabel trainTuplesLabel = new JLabel("No training data loaded");
        temp.add(trainTuplesLabel);

        JFileChooser fc = new JFileChooser();
        final JButton selectTrainingData = new JButton("Select Training Data");
        selectTrainingData.addActionListener(e1 -> {
            int returnVal = fc.showOpenDialog(NNParamsPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                trainingTuples = NNTrainingDataLoader.loadTrainingTuples(file);
                if( trainingTuples != null )
                    if( !trainingTuples.isEmpty() ) {
                        run.setEnabled(true);
                        trainTuplesLabel.setText(trainingTuples.size() + " training tuples loaded");
                        inputsTextBox.setText(trainingTuples.get(0).getInputs().size()+"");
                        outputsTextBox.setText(trainingTuples.get(0).getExpectedOutputs().size()+"");
                    }
            }
        });
        temp.add(selectTrainingData);


        run.setEnabled(false);
        run.addActionListener(e -> new Thread(()->{
            run.setEnabled(false);
            stop.setEnabled(true);
            loadNNButton.setEnabled(false);
            saveNNButton.setEnabled(false);
            clear.setEnabled(false);


            sbpNNExperiment = new SBPNNExperiment(getNNExpParams());
            sbpNNExperiment.setTrainingTuples(trainingTuples);

            if( nnToUse != null )
                sbpNNExperiment.setNeuralNet(nnToUse);
            else
                sbpNNExperiment.init();

            nnToUse = sbpNNExperiment.getNeuralNet();
            nnToUse.setNetworkError(Double.MAX_VALUE);
            clear.setVisible(true);

            sbpNNExperiment.setUpdateListener(sbpState -> {
                if( !updateLabel ) return;
                SwingUtilities.invokeLater(()-> {
                    infoLabel.setText(String.format("<html>Status: Epoch: %d Iteration: %d<br> Error: %f<br>Over %d tuples<br>%f Error per tuple (avg)",
                            sbpState.epoch, sbpState.iteration, sbpState.bestError,trainingTuples.size(),sbpState.bestError/trainingTuples.size()));
                });
            });

            System.out.println("Running Experiment!");
            final SBP.SBPResults runResults = sbpNNExperiment.run();
            nnToUse = (NeuralNet) runResults.sbpImpl;

            double error = runResults.networkError;
            System.out.println("Experiment over!");
            System.out.println("Iterations: " + runResults.numberOfIterationsTaken + " Epochs: " + runResults.numberOfEpochs + " Error: " + error);



            SwingUtilities.invokeLater(()->{
                stop.setEnabled(false);
                run.setEnabled(true);
                clear.setEnabled(true);
//                JFrame results = new JFrame();
//                JPanel content = new JPanel();
//                results.setContentPane(content);
//                content.add(new JLabel("<html>Experiment over<br>Resulting network error: " + error +"<html>"));
//                results.setLocationRelativeTo(null);
//                results.setSize(300,200);
//                results.setVisible(true);
            });


        }).start());
        temp.add(run);

        stop.addActionListener(e -> {
            stop.setEnabled(false);
            sbpNNExperiment.stop();
            run.setEnabled(true);
            loadNNButton.setEnabled(true);
            saveNNButton.setEnabled(true);
            clear.setEnabled(true);
        });
        stop.setEnabled(false);
        temp.add(stop);


        clear.addActionListener(e -> {
            stop.doClick();
            nnToUse = null;
            clear.setVisible(false);

            infoLabel.setText("Status: Cleared Loaded NN");
        });
        clear.setVisible(false);
        temp.add(clear);

        add(temp);



        temp = new JPanel();
        fc.setCurrentDirectory(new File("."));

        loadNNButton.addActionListener(e -> {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                infoLabel.setText("<html>Status: Loading NN");

                File file = fc.getSelectedFile();

                nnToUse = NeuralNetIO.loadNN(file);

                if( nnToUse == null ){
                    infoLabel.setText("<html>Status: Unable to load NN from<br>" + file.getName());
                }
                else {
                    clear.setVisible(true);
                    infoLabel.setText("<html>Status: Loaded NN");
                    inputsTextBox.setText(nnToUse.getInputNeurons().size()+"");
                    outputsTextBox.setText(nnToUse.getOutputNeurons().size()+"");

                    StringBuilder sb = new StringBuilder();
                    nnToUse.getHiddenLayers().forEach(hiddenLayer -> sb.append(hiddenLayer.size()).append('-'));
                    String hnpl = sb.toString();
                    if(hnpl.lastIndexOf("-") == hnpl.length()-1)
                        hnpl = hnpl.substring(0,hnpl.length()-1);
                    HidNeuronsPerLayerTextBox.setText(hnpl);

                    biasCheckbox.setSelected(nnToUse.getBiasNeuron()!=null);

                }
            }
        });
        temp.add(loadNNButton);



        saveNNButton.addActionListener(e -> {
            int returnVal = fc.showSaveDialog(NNParamsPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                if( sbpNNExperiment == null ){
                    infoLabel.setText("<html>You must start a run<br> to create the specified NN");
                }
                else {
                    NeuralNetIO.saveNN(sbpNNExperiment.getNeuralNet(), file);
                    infoLabel.setText("<html>Status: NN Saved to:<br> " + file.getName());
                }
            }
        });
        temp.add(saveNNButton);
        add(temp);


        temp = new JPanel();
        JCheckBox updateCB = new JCheckBox("Update this label");
        updateCB.setSelected(true);
        updateCB.addActionListener(e->updateLabel = !updateLabel);
        temp.add(updateCB);
        temp.add(infoLabel);
        add(temp);

    }

    public NNExperimentParams getNNExpParams(){
        double A = Double.parseDouble(ATextBox.getText());
        double B = Double.parseDouble(BTextBox.getText());
        double N = Double.parseDouble(NTextBox.getText());

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

        int hiddenLayers = hNeurons.length;

        NNExperimentParams nnExperimentParams = new NNExperimentParams(A, B, N, useBias, epochs, trainingIterationsPerEpoch, getInputs(), hiddenLayers, desiredErrorRate, alpha, hNeurons);
        nnExperimentParams.setWeightDecay(getWeightDecay());
        nnExperimentParams.setOutputs(getOutputs());
        return nnExperimentParams;
    }

    public int getEpochs() {
        return Integer.parseInt(EpochTextBox.getText());
    }

    public int getTrainingIterations() {
        return Integer.parseInt(TrainingItsTextBox.getText());
    }

    public double getA() {
        return Double.parseDouble(ATextBox.getText());
    }

    public double getB() {
        return Double.parseDouble(BTextBox.getText());
    }
    public double getN() {
        return Double.parseDouble(NTextBox.getText());
    }
    public double getAlpha() {
        return Double.parseDouble(AlphaTextBox.getText());
    }

    public double getHiddenLayers() {
        return HidNeuronsPerLayerTextBox.getText().split("-").length;
    }

    public double getErrorRate() {
        return Double.parseDouble(ErrorRateTextBox.getText());
    }

    public int getInputs() {
        return Integer.parseInt(inputsTextBox.getText());
    }
    public int getOutputs() {
        return Integer.parseInt(outputsTextBox.getText());
    }
    public double getWeightDecay() {
        return Double.parseDouble(weightDecayTextBox.getText());
    }

    public int[] getHiddenNeurons(){
        String[] hNeuronsSpl = HidNeuronsPerLayerTextBox.getText().split("-");
        int[] hNeurons = new int[hNeuronsSpl.length];
        for (int i = 0; i < hNeuronsSpl.length; i++) {
            String num = hNeuronsSpl[i];
            hNeurons[i] = Integer.parseInt(num);
        }
        return hNeurons;
    }

}
