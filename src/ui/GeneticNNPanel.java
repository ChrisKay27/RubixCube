package ui;

import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolImpl;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import cube.RubixCube;
import neuralnet.NNTrainingDataLoader;
import neuralnet.NeuralNet;
import neuralnet.NeuralNetParams;
import neuralnet.TrainingTuple;
import neuralnet.genetic.GeneticNNSolution;
import searches.AStar;
import searches.Searchable;
import training.TrainingDataGenerator;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 4/9/2016.
 */
public class GeneticNNPanel extends JPanel {

    private Connection conn;
    static {
        try {
            // The newInstance() call is a work around for some broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
    }

    private final NNPanel nnPanel;
    private List<TrainingTuple> trainingTuples;
    private final JLabel trainingTuplesLoadedLabel = new JLabel();
    private int runNumber = 0;

    public GeneticNNPanel() {
        GeneticNNSolution geneticNNSolution = new GeneticNNSolution();

        NeuralNetParams params = new NeuralNetParams();
        params.setSigmoid(geneticNNSolution.getSigmoid());
        params.setInputNeurons(2);
        params.setNumberOfHiddenLayers(1);
        params.setHiddenNeuronsInLayer(2);
        params.setOutputNeurons(1);
        params.setBiasNeuron(true);
        NeuralNet neuralNet = new NeuralNet(params);
        neuralNet.init();
        nnPanel = new NNPanel(neuralNet);



//        JPanel northPanel = new JPanel();
        GeneticParamsPanel gpp = new GeneticParamsPanel();



        add(gpp, BorderLayout.NORTH);




        JPanel centerPanel = new JPanel();
        trainingTuplesLoadedLabel.setText("No training tuples loaded.");
        centerPanel.add(trainingTuplesLoadedLabel);

        JLabel infoLabel = new JLabel("Not Started");


        JButton startButton = new JButton("Start");

        ThreadPool tp = new ThreadPoolImpl(1,2,20000,"DatabaseUpdaterPool");

        startButton.addActionListener(e -> {
            gpp.getGaParamsVariations().forEach(gaParams -> {

                int popSize = gaParams.getPopSize();
                int generations = gaParams.getGenerations();
                double percElites = gaParams.getPercElites();
                double percFreaks = gaParams.getPercMutations();
                double percCrossOvers = gaParams.getPercCross();
                new Thread(()-> {

                    NeuralNet nn = geneticNNSolution.run(gaParams,trainingTuples,gr->{
                        infoLabel.setText(String.format("Generation: %d  Worst Fitness: %.3f Avg Fitness: %.3f Best Fitness %.3f", gr.generation,gr.worstFitness,gr.avgFitness,gr.bestFitness));


                        //Used to load results into a database if the connection was successful (which it wont be on your machine)
                        if( conn != null ) {

                            tp.getAnyWorkQueue().addWork(new Work() {
                                @Override
                                public void doWork() {
                                    String query = "INSERT INTO GAResults(runNumber, popSize, generations, percElites, percMutations, percCrossOver, genNumber, lowestFitness, averageFitness, bestFitness) VALUES (?,?,?,?,?,?,?,?,?,?);";
                                    try {
                                        PreparedStatement statement = conn.prepareStatement(query);

                                        statement.setInt(1, runNumber);
                                        statement.setInt(2, popSize);
                                        statement.setInt(3, generations);
                                        statement.setDouble(4, percElites);
                                        statement.setDouble(5, percFreaks);
                                        statement.setDouble(6, percCrossOvers);

                                        statement.executeUpdate();
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                @Override
                                public void setEnqueueTime(long timeInMillis) {
                                }

                                @Override
                                public long getEnqueueTime() {
                                    return 0;
                                }

                                @Override
                                public String getName() {
                                    return null;
                                }
                            });
                        }

                    });

//                    nnPanel.setNn(nn);
//                    nnPanel.update();

                    runNumber++;

                }).start();


            });
        });
        centerPanel.add(startButton);
        centerPanel.add(infoLabel);



        add(centerPanel, BorderLayout.CENTER);
    }

    public List<TrainingTuple> getTrainingTuples() {
        return trainingTuples;
    }

    public void setTrainingTuples(List<TrainingTuple> trainingTuples) {
        this.trainingTuples = trainingTuples;
        trainingTuplesLoadedLabel.setText(trainingTuples.size() + " training tuples loaded.");

    }
}
