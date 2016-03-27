package ui;

import experiment.ExperimentParameters;
import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.Tuple;
import neuralnet.NNTrainingDataLoader;
import neuralnet.TrainingTuple;
import searches.Search;
import training.TrainingDataGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 3/9/2016.
 */
public class RubixCubeExperimentPanel extends JPanel {

    public RubixCubeExperimentPanel(Consumer<List<TrainingTuple>> trainingTupleConsumer) {

        setBorder(BorderFactory.createTitledBorder("Rubix Cube Data Generator"));

        JPanel rubixCubePanel = new JPanel(new GridLayout(3,2,5,5));

        JPanel temp = new JPanel();
        JLabel numberOfRunsLabel = new JLabel("Number of Runs");
        JTextField numberOfRunsField = new JTextField("10",15);
        numberOfRunsLabel.setLabelFor(numberOfRunsField);
        temp.add(numberOfRunsLabel);
        temp.add(numberOfRunsField);
        rubixCubePanel.add(temp);

        temp = new JPanel();
        JLabel cubeSizeLabel = new JLabel("Cube Size:");
        JTextField cubeSizeField = new JTextField("3",15);
        temp.add(cubeSizeLabel);
        temp.add(cubeSizeField);
        rubixCubePanel.add(temp);

        temp = new JPanel();
        JLabel movesLabel = new JLabel("Random moves: ");
        JTextField movesField = new JTextField("10",15);
        temp.add(movesLabel);
        temp.add(movesField);
        rubixCubePanel.add(temp);

        temp = new JPanel();
        JLabel giveUpAfterStatesLabel = new JLabel("Give up after states encountered: ");
        JTextField giveUpAfterStatesField = new JTextField("2000000",15);
        temp.add(giveUpAfterStatesLabel);
        temp.add(giveUpAfterStatesField);
        rubixCubePanel.add(temp);

        temp = new JPanel();
        JComboBox<Search.Searches> searches = new JComboBox<>();
        searches.addItem(Search.Searches.ASTAR);
        searches.addItem(Search.Searches.BFS);
        temp.add(searches);
        rubixCubePanel.add(temp);


        JLabel statusLabel = new JLabel("Status: not started");

        temp = new JPanel();
        JButton runExp = new JButton("Run RubixCubeExperiment!");
        runExp.addActionListener(e -> {

            Thread t = new Thread() {
                @Override
                public void run() {
                    int numberOfRuns = Integer.parseInt(numberOfRunsField.getText());

                    int cubeSize = Integer.parseInt(cubeSizeField.getText());
                    int giveUpAfter = Integer.parseInt(giveUpAfterStatesField.getText());
                    int moves = Integer.parseInt(movesField.getText());

                    System.out.println("number of runs=" + numberOfRuns);



                    for (int i = 0; i < numberOfRuns; i++) {
                        statusLabel.setText("Status: Running iteration: "+ i);

                        System.out.println("run # " + i );
                        ExperimentParameters expParams = new ExperimentParameters((Search.Searches) searches.getSelectedItem(), cubeSize, giveUpAfter, moves);
                        expParams.setPathTracer(s->{});

                        RubixCubeExperiment exp = new RubixCubeExperiment(expParams);

                        System.out.println("Beginning RubixCubeExperiment!");
                        List<Tuple> resultTuples = exp.runExperiment();

                        if( resultTuples != null )
                            ExperimentResultsWriter.writeResultsToFile("results2", cubeSize, resultTuples);
                    }

                    File outputFile = new File(cubeSize+"x"+cubeSize+"x"+cubeSize+"-trainingData.csv");
                    int count=2;
                    while( outputFile.exists() )
                        outputFile = new File(cubeSize+"x"+cubeSize+"x"+cubeSize+"-trainingData"+count+++".csv");

                    new TrainingDataGenerator(cubeSize,"results2", outputFile.getName()).generateTrainingData();


                    File results2 = new File("results2");
                    File[] files = results2.listFiles();
                    if( files != null )
                        for( File f: files )
                            f.delete();
                    boolean deleted = results2.delete();


                    List<TrainingTuple> trainingData = NNTrainingDataLoader.loadTrainingTuples(outputFile);
                    trainingTupleConsumer.accept(trainingData);

                    statusLabel.setText("Status: Training Data saved to file: " + outputFile.getName());
                }
            };
            t.setDaemon(true);
            t.start();
        });
        temp.add(runExp);
        rubixCubePanel.add(temp);


        JButton genTrainDataButton = new JButton("Select output file");
        genTrainDataButton.addActionListener(e -> {

            Thread t = new Thread() {
                @Override
                public void run() {
                    int cubeSize = Integer.parseInt(cubeSizeField.getText());

                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(new File("."));

                    int returnVal = fc.showOpenDialog(RubixCubeExperimentPanel.this);
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

        rubixCubePanel.add(statusLabel);

        add(rubixCubePanel);
    }
}
