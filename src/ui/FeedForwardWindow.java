package ui;

import neuralnet.NeuralNet;
import neuralnet.TrainingTuple;

import javax.swing.*;
import java.util.List;

/**
 * Created by chris_000 on 3/18/2016.
 */
public class FeedForwardWindow extends JFrame {

    public FeedForwardWindow(NeuralNet nnToUse, List<TrainingTuple> trainingTuples) {

        setSize(400,400);
        setLocationRelativeTo(null);

        JPanel content = new JPanel();

        if( nnToUse == null ) {
            add(new JLabel("You must have a NN loaded to do this"));
            setVisible(true);
            return;
        }
        if( trainingTuples == null || trainingTuples.isEmpty() ) {
            add(new JLabel("You must have training tuples loaded to do this"));
            setVisible(true);
            return;
        }

        TextBox indexTB = new TextBox("Training Tuple Index","0",8);

        JLabel outputLabel = new JLabel("Output:");
        JLabel expectedOutputLabel = new JLabel("Expected Output:");


        JButton ffButton = new JButton("Feed Forward");
        ffButton.addActionListener(e -> {
            TrainingTuple trainingTuple = trainingTuples.get(indexTB.getInt());


            List<Double> output = nnToUse.feedForward(trainingTuple.getInputs());

            StringBuilder sb = new StringBuilder();
            sb.append("Output: ");
            output.forEach(d -> sb.append(String.format("%.2f,",d)));
            outputLabel.setText(sb.toString());

            StringBuilder sb2 = new StringBuilder();
            sb2.append("Expected Output: ");
            trainingTuple.getExpectedOutputs().forEach(d -> sb2.append(d).append(","));
            expectedOutputLabel.setText(sb2.toString());

        });

        content.add(indexTB);
        content.add(outputLabel);
        content.add(expectedOutputLabel);
        content.add(ffButton);
        add(content);

        setVisible(true);

    }
}
