package ui;

import experiment.ExperimentParameters;
import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.Tuple;
import neuralnet.NeuralNetIO;
import searches.Search;
import training.TrainingDataGenerator;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Created by chris_000 on 3/9/2016.
 */
public class GenerateTrainingDataPanel extends JPanel {

    public GenerateTrainingDataPanel() {
        setBorder(BorderFactory.createTitledBorder("Generate Training Data from Results Folder files!"));

        JPanel rubixCubePanel = new JPanel();

        JLabel cubeSizeLabel = new JLabel("Cube Size: ");
        JTextField cubeSizeField = new JTextField("3",15);
        cubeSizeLabel.setLabelFor(cubeSizeField);

        JLabel statusLabel = new JLabel("Status: Not started");


        JButton genTrainDataButton = new JButton("Select output file");
        genTrainDataButton.addActionListener(e -> {

            Thread t = new Thread() {
                @Override
                public void run() {
                    int cubeSize = Integer.parseInt(cubeSizeField.getText());

                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(new File("."));

                    int returnVal = fc.showOpenDialog(GenerateTrainingDataPanel.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        new TrainingDataGenerator(cubeSize,"results",file.getPath()).generateTrainingData();

                        statusLabel.setText("Status: Complete");
                    }
                }
            };
            t.setDaemon(true);
            t.start();
        });


        rubixCubePanel.add(cubeSizeLabel);
        rubixCubePanel.add(cubeSizeField);
        rubixCubePanel.add(genTrainDataButton);
        rubixCubePanel.add(statusLabel);

        add(rubixCubePanel);
    }
}
