package ui;

import cube.RubixCube;
import experiment.Tuple;
import neuralnet.NNTrainingDataLoader;
import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import neuralnet.TrainingTuple;
import neuralnet.genetic.GeneticNNSolution;
import searches.AStar;
import searches.Searchable;
import searches.Searchable.EdgeChildPair;
import training.TrainingDataGenerator;
import xor.genetic.GeneticXorSolution;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Chris on 4/9/2016.
 */
public class GeneticNNSolutionPanel extends JPanel {

    private NNPanel nnPanel;

    public GeneticNNSolutionPanel() {
        super(new GridLayout(3,1));
        
        
        GeneticNNPanel geneticNNPanel = new GeneticNNPanel();
        add(new RubixCubeExperimentPanel(geneticNNPanel::setTrainingTuples));



        add(geneticNNPanel);
    }
}
