package ui;

import neuralnet.NNExperimentParams;
import neuralnet.NeuralNet;
import neuralnet.NeuralNetIO;
import xor.XORProblem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
    private final JTextField HiddenLayersTextBox;
    private final JTextField HidNeuronsPerLayerTextBox;
    private final JTextField EpochTextBox;

    public NNParamsPanel( boolean showCreateButton, boolean showLoadButton, Consumer<NNExperimentParams> NNCreationListener, Consumer<NeuralNet> NNLoadedListener) {
        super(new GridLayout(2,10));

        JPanel temp = new JPanel();
        JLabel ALabel = new JLabel("A:");
        ATextBox = new JTextField("1.716",10);
        ALabel.setLabelFor(ATextBox);
        temp.add(ALabel);
        temp.add(ATextBox);
        add(temp);

        temp = new JPanel();
        JLabel BLabel = new JLabel("B:");
        BTextBox = new JTextField("0.667",10);
        BLabel.setLabelFor(BTextBox);
        temp.add(BLabel);
        temp.add(BTextBox);
        add(temp);

        temp = new JPanel();
        JLabel NLabel = new JLabel("Learning Rate:");
        NTextBox = new JTextField("0.125",10);
        NLabel.setLabelFor(NTextBox);
        temp.add(NLabel);
        temp.add(NTextBox);
        add(temp);

        temp = new JPanel();
        JLabel alphaLabel = new JLabel("Momentum Alpha:");
        AlphaTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(AlphaTextBox);
        temp.add(alphaLabel);
        temp.add(AlphaTextBox);
        add(temp);

        temp = new JPanel();
        JLabel TrainingItsLabel = new JLabel("Training Iterations:");
        TrainingItsTextBox = new JTextField("1000",10);
        TrainingItsLabel.setLabelFor(TrainingItsTextBox);
        temp.add(TrainingItsLabel);
        temp.add(TrainingItsTextBox);
        add(temp);

        temp = new JPanel();
        JLabel EpochLabel = new JLabel("Training Epochs:");
        EpochTextBox = new JTextField("100",6);
        EpochLabel.setLabelFor(EpochTextBox);
        temp.add(EpochLabel);
        temp.add(EpochTextBox);
        add(temp);

        temp = new JPanel();
        JLabel HiddenLayersLabel = new JLabel("Hidden Layers:");
        HiddenLayersTextBox = new JTextField("1",10);
        HiddenLayersLabel.setLabelFor(HiddenLayersTextBox);
        temp.add(HiddenLayersLabel);
        temp.add(HiddenLayersTextBox);
        add(temp);

        temp = new JPanel();
        JLabel HidNeuronsPerLayerLabel = new JLabel("Hidden Neurons Per Layer ('2-3' if you want a 2-2-3-1 NN):");
        HidNeuronsPerLayerTextBox = new JTextField("2",10);
        HidNeuronsPerLayerLabel.setLabelFor(HidNeuronsPerLayerTextBox);
        temp.add(HidNeuronsPerLayerLabel);
        temp.add(HidNeuronsPerLayerTextBox);
        add(temp);

        temp = new JPanel();
        JLabel ErrorRateLabel = new JLabel("Error Rate:");
        ErrorRateTextBox = new JTextField("0.000001",20);
        ErrorRateLabel.setLabelFor(ErrorRateTextBox);
        temp.add(ErrorRateLabel);
        temp.add(ErrorRateTextBox);
        add(temp);

        if(showCreateButton) {
            temp = new JPanel();
            final JButton createNNButton = new JButton("Create NN");
            createNNButton.addActionListener(e -> {
                NNCreationListener.accept(getNNExpParams());
            });
            temp.add(createNNButton);
            add(temp);
        }

        if(showLoadButton) {
            temp = new JPanel();
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("."));
            final JButton loadNNButton = new JButton("Load NN");
            loadNNButton.addActionListener(e -> {

                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    NNLoadedListener.accept(NeuralNetIO.loadNN(file));
                }
            });
            temp.add(loadNNButton);
            add(temp);
        }
    }

    public NNExperimentParams getNNExpParams(){
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
        return new NNExperimentParams(A, B, N, true, epochs, trainingIterationsPerEpoch, hiddenLayers, desiredErrorRate, alpha, hNeurons);
    }
}
