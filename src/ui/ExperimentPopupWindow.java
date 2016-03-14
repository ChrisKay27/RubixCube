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
public class ExperimentPopupWindow extends JFrame {



    public ExperimentPopupWindow() {

        JPanel contentPane = new JPanel(new GridLayout(3,1,5,5));

        contentPane.add(new RubixCubeExperimentPanel());

        contentPane.add(new GenerateTrainingDataPanel());

        contentPane.add(new NNParamsPanel());


        setContentPane(contentPane);

        setSize(1000,900);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
    }







}
