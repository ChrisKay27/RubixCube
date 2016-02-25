package ui;

import experiment.Tuple;

import javax.swing.*;
import java.util.List;

/**
 *
 * Created by Chris on 1/29/2016.
 */
public class ExperimentCompletePopupWindow extends JFrame {
    public ExperimentCompletePopupWindow(int numTuplesAdded) {
        setSize(300,200);

        JLabel l = new JLabel(numTuplesAdded + " results have been added to the results folder in non-encoded form," +
                " run this program with the option -genTrainingData to convert these tuples into training data input.");

        add(l);

        setVisible(true);

    }
}
