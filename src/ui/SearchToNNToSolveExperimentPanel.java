package ui;

import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.ExperimentParameters;
import experiment.Tuple;
import searches.Search.Searches;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Chris on 1/26/2016.
 */
public class SearchToNNToSolveExperimentPanel extends JPanel {



    public SearchToNNToSolveExperimentPanel() {
        super(new GridLayout(3,1,5,5));

        add(new RubixCubeExperimentPanel());

        add(new GenerateTrainingDataPanel());

        add(new NNParamsPanel());
    }







}
