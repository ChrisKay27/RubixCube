package ui;

import neuralnet.NNExperimentParams;
import neuralnet.NNTrainingDataLoader;
import neuralnet.TrainingTuple;
import sbp.SBP;
import sbp.SBPNNExperiment;
import util.Pair;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chris_000 on 3/16/2016.
 */
public class Phase3ExperimentPanel extends JPanel {



    private Connection conn;
    static {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            // handle the error
        }
    }


    private final TextBox LRTB;
    private final TextBox LRtoTB;
    private final TextBox LRincTB;
    private final TextBox MTB;
    private final TextBox MtoTB;
    private final TextBox MincTB;
    private final TextBox epochsTB;
    private final TextBox epochstoTB;
    private final TextBox epochsincTB;
    private final TextBox iterTB;
    private final TextBox iterToTB;
    private final TextBox itersIncTB;
    private final JLabel progess;
    private final TextBox hiddenLayerSize;
    private final TextBox hiddentoTB;
    private final TextBox hiddenIncTB;
    private final TextBox inputsTB;
    private final TextBox outputsTB;

    private boolean stopFlag = false;
    private SBPNNExperiment sbpnnExperiment;

    public Phase3ExperimentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/nn_experiment_results?" +
                    "user=Chris&password=rubixcube");
        } catch (SQLException ex) {
            // handle any errors
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
        }

        {
            inputsTB = new TextBox("Inputs:", "324", 5);
            add(inputsTB);
        }
        {
            outputsTB = new TextBox("Outputs:", "7", 5);
            add(outputsTB);
        }
        {
            JPanel temp = new JPanel();
            hiddenLayerSize = new TextBox("Hidden Layer Size:", "2", 3);
            hiddentoTB = new TextBox("To", "64", 3);
            hiddenIncTB = new TextBox("in increments of", "8", 3);
            temp.add(hiddenLayerSize);
            temp.add(hiddentoTB);
            temp.add(hiddenIncTB);
            add(temp);
        }
        {
            JPanel temp = new JPanel();
            LRTB = new TextBox("Learning Rate in ", "0.05", 5);
            LRtoTB = new TextBox("To", "0.20", 3);
            LRincTB = new TextBox("in increments of", "0.05", 3);
            temp.add(LRTB);
            temp.add(LRtoTB);
            temp.add(LRincTB);
            add(temp);
        }
        {
            JPanel temp = new JPanel();
            MTB = new TextBox("Momentum rate in ", "0.0", 5);
            MtoTB = new TextBox("To", "0.3", 3);
            MincTB = new TextBox("in increments of", "0.1", 3);
            temp.add(MTB);
            temp.add(MtoTB);
            temp.add(MincTB);
            add(temp);
        }
        {
            JPanel temp = new JPanel();
            epochsTB = new TextBox("Epoch start", "1", 5);
            epochstoTB = new TextBox("Epoch end", "10", 3);
            epochsincTB = new TextBox("in increments of", "9", 3);
            temp.add(epochsTB);
            temp.add(epochstoTB);
            temp.add(epochsincTB);
            add(temp);
        }
        {
            JPanel temp = new JPanel();
            iterTB = new TextBox("Iterations (* training data set size)", "1", 5);
            iterToTB = new TextBox("Iterations end ", "201", 3);
            itersIncTB = new TextBox("in increments of", "100", 3);
            temp.add(iterTB);
            temp.add(iterToTB);
            temp.add(itersIncTB);
            add(temp);
        }

        {
            JButton startbutton = new JButton("Select Training Data and Start");
            progess = new JLabel("Not started");
            JLabel currentParams = new JLabel("Current Params:");

            JButton stopbutton = new JButton("Stop");
            List<Pair<Double,String>> resultsList = new ArrayList<>();

            startbutton.addActionListener(e-> new Thread(()->{


                JFileChooser fc = new JFileChooser(".");
                int res = fc.showOpenDialog(Phase3ExperimentPanel.this);

                if( res == JFileChooser.APPROVE_OPTION ) {
                    startbutton.setEnabled(false);
                    stopbutton.setEnabled(true);

                    int inputs = getInputSize();
                    int outputs = getOutputsSize();


                    int hLayerStartSize = getHiddenLayerStartSize();
                    int hLayerInc = getHiddenLayerInc();
                    int hLayerEnd = getHiddenLayerEndSize();

                    double learningRateStart = getLearningRateStart();
                    double learningRateInc = getLearningRateInc();
                    double learningRateEnd = getLearningRateEnd();

                    double momentumStart = getMomentumStart();
                    double momentumInc = getMomentumInc();
                    double momentumEnd = getMomentumEnd();

                    int epochsStart = getEpochsStart();
                    int epochsInc = getEpochsInc();
                    int epochsEnd = getEpochsEnd();

                    int iterStart = getIterations();
                    int iterInc = getIterationsInc();
                    int iterEnd = getIterationsEnd();


                    List<TrainingTuple> trainingTuples = NNTrainingDataLoader.loadTrainingTuples(fc.getSelectedFile());

                    progess.setText("Starting with " + trainingTuples.size() + " training tuples");

                    lab:
                    for (int hlayerSize = hLayerStartSize; hlayerSize <= hLayerEnd; hlayerSize += hLayerInc) {
                        for (double learningRate = learningRateStart; learningRate <= learningRateEnd; learningRate += learningRateInc) {
                            for (double momentum = momentumStart; momentum <= momentumEnd; momentum += momentumInc) {
                                for (int epochs = epochsStart; epochs <= epochsEnd; epochs += epochsInc) {
                                    for (int iter = iterStart; iter <= iterEnd; iter += iterInc) {
                                        if( stopFlag )
                                            break lab;

                                        String s = "hlayerSize = " + hlayerSize + " learningRate = " + learningRate + " momentum = " + momentum + " epochs = " + epochs + " iter = " + iter;

                                        currentParams.setText(s);
                                        NNExperimentParams nnExperimentParams = new NNExperimentParams(1.716, .667, learningRate, true, epochs, trainingTuples.size()*iter, inputs, 1, outputs, 0.00001, momentum, hlayerSize);

                                        sbpnnExperiment = new SBPNNExperiment(nnExperimentParams);

                                        sbpnnExperiment.init();
                                        sbpnnExperiment.setTrainingTuples(trainingTuples);
                                        SBP.SBPResults results = sbpnnExperiment.run();

                                        if( stopFlag )
                                            break lab;

                                        s = s+" Error: " + results.networkError;

//                                        progess.setText(s);
                                        System.out.println(s);

                                        resultsList.add(new Pair<>(results.networkError,s));

                                        //Used to load results into a database if the connection was successful (which it wont be on your machine)
                                        if( conn != null ) {
                                            int hLayerSize2 = hlayerSize;
                                            double learningRate2 = learningRate;
                                            double momentum2 = momentum;
                                            int epochs2 = epochs;
                                            int iter2 = iter;


                                            new Thread(() -> {
                                                try {
                                                    String query = "INSERT INTO error(error,hLayerSize,learningRate,momentum,epochs,iterations) VALUES (?,?,?,?,?,?);";

                                                    PreparedStatement statement = conn.prepareStatement(query);

                                                    statement.setDouble(1, results.networkError);
                                                    statement.setInt(2, hLayerSize2);
                                                    statement.setDouble(3, learningRate2);
                                                    statement.setDouble(4, momentum2);
                                                    statement.setInt(5, epochs2);
                                                    statement.setInt(6, iter2);

                                                    statement.executeUpdate();
                                                } catch (SQLException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }).start();
                                        }

                                    }
                                    progess.setText("hlayerSize = " + hlayerSize + " learningRate = " + learningRate + " momentum = " + momentum + " epochs = " + epochs);
                                }
                            }
                        }
                    }

                    stopbutton.doClick();
                }

            }).start());

            stopbutton.addActionListener(e->{
                stopFlag = true;
                if( sbpnnExperiment != null )
                    sbpnnExperiment.stop();

                Collections.sort(resultsList, (o1, o2) -> {
                    if( o1.first > o2.first ) return -1;
                    else if( o1.first < o2.first ) return 1;
                    else return 0;
                });

                File f = new File("nestedLoopsResults.txt");
                int c=2;
                while( f.exists() )
                    f = new File("nestedLoopsResults"+c+++".txt");

                try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
                    for( Pair<?,String> p : resultsList)
                        bw.write(p.second + '\n');

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                progess.setText("Exp results written to file: " + f.getName());

                startbutton.setEnabled(true);
                stopbutton.setEnabled(false);
            });
            stopbutton.setEnabled(false);

            add(startbutton);
            add(stopbutton);
            add(progess);
            add(currentParams);

        }
    }



    private int getHiddenLayerStartSize(){
        return Integer.parseInt(hiddenLayerSize.getText());
    }
    private int getHiddenLayerEndSize(){
        return Integer.parseInt(hiddentoTB.getText());
    }
    private int getHiddenLayerInc(){
        return Integer.parseInt(hiddenIncTB.getText());
    }

    private int getInputSize(){
        return Integer.parseInt(inputsTB.getText());
    }
    private int getOutputsSize(){
        return Integer.parseInt(outputsTB.getText());
    }



    private double getLearningRateStart(){
        return Double.parseDouble(LRTB.getText());
    }
    private double getLearningRateEnd(){
        return Double.parseDouble(LRtoTB.getText());
    }
    private double getLearningRateInc(){
        return Double.parseDouble(LRincTB.getText());
    }


    private double getMomentumStart(){
        return Double.parseDouble(MTB.getText());
    }
    private double getMomentumEnd(){
        return Double.parseDouble(MtoTB.getText());
    }
    private double getMomentumInc(){
        return Double.parseDouble(MincTB.getText());
    }


    private int getEpochsStart(){
        return Integer.parseInt(epochsTB.getText());
    }
    private int getEpochsEnd(){
        return Integer.parseInt(epochstoTB.getText());
    }
    private int getEpochsInc(){
        return Integer.parseInt(epochsincTB.getText());
    }


    private int getIterations(){
        return Integer.parseInt(iterTB.getText());
    }
    private int getIterationsEnd(){
        return Integer.parseInt(iterToTB.getText());
    }
    private int getIterationsInc(){
        return Integer.parseInt(itersIncTB.getText());
    }



}
