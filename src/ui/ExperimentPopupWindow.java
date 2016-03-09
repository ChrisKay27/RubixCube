package ui;

import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.ExperimentParameters;
import experiment.Tuple;
import searches.Search.Searches;

import javax.swing.*;
import java.util.List;

/**
 * Created by Chris on 1/26/2016.
 */
public class ExperimentPopupWindow extends JFrame {



    public ExperimentPopupWindow(Display display) {

        JPanel contentPane = new JPanel();

        JLabel numberOfRunsLabel = new JLabel("Number of Runs");
        JTextField numberOfRunsField = new JTextField("10",15);
        numberOfRunsLabel.setLabelFor(numberOfRunsField);

        JLabel cubeSizeLabel = new JLabel("Cube Size:");
        JTextField cubeSizeField = new JTextField("3",15);

        JLabel movesLabel = new JLabel("Random moves: ");
        JTextField movesField = new JTextField("10",15);

        JLabel giveUpAfterStatesLabel = new JLabel("Give up after states encountered: ");
        JTextField giveUpAfterStatesField = new JTextField("2000000",15);

        JComboBox<Searches> searches = new JComboBox<>();
        searches.addItem(Searches.ASTAR);
        searches.addItem(Searches.BFS);


        JCheckBox useCurrentCube = new JCheckBox("Use current cube as start cube: ");



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
                        System.out.println("run # " + i );
                        ExperimentParameters expParams = new ExperimentParameters((Searches) searches.getSelectedItem(), cubeSize, giveUpAfter, moves);

                        if( useCurrentCube.isSelected() )
                            expParams.setStartState(display.getDisplayedCube());



                        expParams.setPathTracer(display.getPathTracer());
                        expParams.setTooManyStatesListener(() -> {
                            Display.restartProgram();
                            System.exit(0);
                        });

                        RubixCubeExperiment exp = new RubixCubeExperiment(expParams);

                        display.setSearchDiagnostic(exp.getSearchDiagnostic());
                        display.setExperiment(exp);


                        System.out.println("Beginning RubixCubeExperiment!");
                        List<Tuple> resultTuples = exp.runExperiment();

                        if( resultTuples != null ) {
                            ExperimentResultsWriter.writeResultsToFile("results", cubeSize, resultTuples);

//                            new ExperimentCompletePopupWindow(resultTuples.size());

                            display.updateResultsPanel(resultTuples);
                        }

                        display.possiblyStartAnotherRun();
                    }
                }
            };
            t.setDaemon(true);
            t.start();
            ExperimentPopupWindow.this.dispose();
        });


        contentPane.add(numberOfRunsLabel);
        contentPane.add(numberOfRunsField);
        contentPane.add(cubeSizeLabel);
        contentPane.add(cubeSizeField);
        contentPane.add(movesLabel);
        contentPane.add(movesField);
        contentPane.add(giveUpAfterStatesLabel);
        contentPane.add(giveUpAfterStatesField);

        contentPane.add(searches);
        contentPane.add(useCurrentCube);



        contentPane.add(runExp);

        setContentPane(contentPane);

        setSize(500,400);
        setVisible(true);
    }







}
