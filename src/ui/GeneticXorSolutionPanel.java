package ui;

import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import xor.genetic.GeneticXorSolution;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created by Chris on 4/9/2016.
 */
public class GeneticXorSolutionPanel extends JPanel {


    private final NNPanel nnPanel;

    public GeneticXorSolutionPanel() {
        super(new BorderLayout());

        GeneticXorSolution geneticXorSolution = new GeneticXorSolution();

        NeuralNetParams params = new NeuralNetParams();
        params.setSigmoid(geneticXorSolution.getSigmoid());
        params.setInputNeurons(2);
        params.setNumberOfHiddenLayers(1);
        params.setHiddenNeuronsInLayer(2);
        params.setOutputNeurons(1);
        params.setBiasNeuron(true);
        NeuralNet neuralNet = new NeuralNet(params);
        neuralNet.init();
        nnPanel = new NNPanel(neuralNet);

        add(nnPanel.getGraphComponent(),BorderLayout.CENTER);



        JPanel topPanel = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            new GeneticParamsFrame(gaParams -> {
                new Thread(()-> {
                    NeuralNet nn = geneticXorSolution.run(gaParams);
                    nnPanel.setNn(nn);
                    nnPanel.update();
                }).start();
            });
        });
        topPanel.add(startButton);

        add(topPanel,BorderLayout.NORTH);

        JLabel TTLabel = new JLabel("Training Tuple:");
        JTextField TTField = new JTextField("-1,-1",20);
        TTLabel.setLabelFor(TTField);
        topPanel.add(TTField);
        topPanel.add(TTLabel);

        JLabel OutputLabel = new JLabel("Output: ");
        topPanel.add(OutputLabel);

        JLabel FILLER = new JLabel("      ");
        topPanel.add(FILLER);

        JButton FFButton = new JButton("Feed It Forward");
        FFButton.addActionListener(e -> {
            java.util.List<Double> inputs = new ArrayList<>();
            String[] ttSplit = TTField.getText().split(",");
            for(String input : ttSplit)
                inputs.add(Double.parseDouble(input));

            java.util.List<Double> outputs = nnPanel.feedForward(inputs);

            StringBuilder sb = new StringBuilder();
            outputs.forEach(output -> sb.append(String.format("%1.1f,",output<0?-1.0:1)));
            OutputLabel.setText("Output: " + sb.substring(0,sb.length()-1));
        });
        topPanel.add(FFButton);


    }
}
