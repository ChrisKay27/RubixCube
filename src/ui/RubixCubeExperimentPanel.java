package ui;

import experiment.ExperimentParameters;
import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.Tuple;
import searches.Search;

import javax.swing.*;
import java.util.List;

/**
 * Created by chris_000 on 3/9/2016.
 */
public class RubixCubeExperimentPanel extends JPanel {

    public RubixCubeExperimentPanel() {
        setBorder(BorderFactory.createTitledBorder("Rubix Cube Data Generator"));
        JPanel rubixCubePanel = new JPanel();

        JLabel numberOfRunsLabel = new JLabel("Number of Runs");
        JTextField numberOfRunsField = new JTextField("10",15);
        numberOfRunsLabel.setLabelFor(numberOfRunsField);

        JLabel cubeSizeLabel = new JLabel("Cube Size:");
        JTextField cubeSizeField = new JTextField("3",15);

        JLabel movesLabel = new JLabel("Random moves: ");
        JTextField movesField = new JTextField("10",15);

        JLabel giveUpAfterStatesLabel = new JLabel("Give up after states encountered: ");
        JTextField giveUpAfterStatesField = new JTextField("2000000",15);

        JComboBox<Search.Searches> searches = new JComboBox<>();
        searches.addItem(Search.Searches.ASTAR);
        searches.addItem(Search.Searches.BFS);

        JLabel statusLabel = new JLabel("Status: not started");

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
                            ExperimentResultsWriter.writeResultsToFile("results", cubeSize, resultTuples);
                    }
                    statusLabel.setText("Status: Complete");
                }
            };
            t.setDaemon(true);
            t.start();
        });


        rubixCubePanel.add(numberOfRunsLabel);
        rubixCubePanel.add(numberOfRunsField);
        rubixCubePanel.add(cubeSizeLabel);
        rubixCubePanel.add(cubeSizeField);
        rubixCubePanel.add(movesLabel);
        rubixCubePanel.add(movesField);
        rubixCubePanel.add(giveUpAfterStatesLabel);
        rubixCubePanel.add(giveUpAfterStatesField);
        rubixCubePanel.add(searches);
        rubixCubePanel.add(runExp);
        rubixCubePanel.add(statusLabel);

        add(rubixCubePanel);
    }
}
