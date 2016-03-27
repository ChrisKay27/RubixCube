package ui;

import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.ExperimentParameters;
import experiment.Tuple;
import main.Main;
import neuralnet.NNTrainingDataLoader;
import searches.Search.Searches;
import training.TrainingDataGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Chris on 1/26/2016.
 */
public class SearchToNNToSolveExperimentPanel extends JPanel {


    private final NNParamsPanel nnParamsPanel;

    public SearchToNNToSolveExperimentPanel() {
        super(new GridLayout(2,1,5,5));
        JPanel top = new JPanel(new GridLayout(2,1,1,1));

        nnParamsPanel = new NNParamsPanel();
        top.add(new RubixCubeExperimentPanel(nnParamsPanel::setTrainingTuples));
        top.add(nnParamsPanel);
        add(top);

        add( new RubixCubePanel(this::cubeNNInterface));

    }

    public String cubeNNInterface(int cubeSize,String cubeState){
        String encodedCubeState = TrainingDataGenerator.getCubeStateTT(cubeState);
        List<Double> inputs = NNTrainingDataLoader.toDoubleList(encodedCubeState);
        List<Double> outputs = nnParamsPanel.feedForward(inputs);

        return Main.getMoveFromNNOutput(cubeSize,outputs);
    }








}
