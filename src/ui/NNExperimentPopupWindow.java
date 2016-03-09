package ui;

import neuralnet.NNExperimentParams;
import sbp.SBP.SBPResults;

import sbp.SBPNNExperiment;
import xor.XORResultsWriter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chris_000 on 2/24/2016.
 */
public class NNExperimentPopupWindow extends JFrame {


    private final JTextField EpochTextBox;
    private final JTextField NIncTextBox;
    private final JTextField NMinTextBox;

    private final JTextField NMaxTextBox;
    private final JTextField AlphaMinTextBox;
    private final JTextField AlphaMaxTextBox;
    private final JTextField AlphaIncTextBox;
    private final JTextField TrainingItsMinTextBox;
    private final JTextField TrainingItsMaxTextBox;
    private final JTextField TrainingItsIncTextBox;


    public NNExperimentPopupWindow(Display display) {
        JPanel contentPane = new JPanel();
        JPanel temp;
        JLabel label;

        {
            temp = new JPanel();
            label = new JLabel("Learning Rate Min:");
            NMinTextBox = new JTextField("0.125", 10);
            label.setLabelFor(NMinTextBox);
            temp.add(label);
            temp.add(NMinTextBox);

            label = new JLabel("Learning Rate Max:");
            NMaxTextBox = new JTextField("0.5", 10);
            label.setLabelFor(NMaxTextBox);
            temp.add(label);
            temp.add(NMaxTextBox);

            label = new JLabel("Learning Rate Incr:");
            NIncTextBox = new JTextField("0.005", 10);
            label.setLabelFor(NIncTextBox);
            temp.add(label);
            temp.add(NIncTextBox);
        }
        contentPane.add(temp);

        {
            temp = new JPanel();
            label = new JLabel("Momentum Alpha Min:");
            AlphaMinTextBox = new JTextField("0.005", 10);
            label.setLabelFor(AlphaMinTextBox);
            temp.add(label);
            temp.add(AlphaMinTextBox);

            label = new JLabel("Momentum Alpha Max:");
            AlphaMaxTextBox = new JTextField("0.5", 10);
            label.setLabelFor(AlphaMaxTextBox);
            temp.add(label);
            temp.add(AlphaMaxTextBox);

            label = new JLabel("Momentum Alpha Incr:");
            AlphaIncTextBox = new JTextField("0.005", 10);
            label.setLabelFor(AlphaIncTextBox);
            temp.add(label);
            temp.add(AlphaIncTextBox);
        }
        contentPane.add(temp);

        {
            temp = new JPanel();
            label = new JLabel("Training Iterations Min:");
            TrainingItsMinTextBox = new JTextField("100", 10);
            label.setLabelFor(TrainingItsMinTextBox);
            temp.add(label);
            temp.add(TrainingItsMinTextBox);

            label = new JLabel("Training Iterations Max:");
            TrainingItsMaxTextBox = new JTextField("1000", 10);
            label.setLabelFor(TrainingItsMaxTextBox);
            temp.add(label);
            temp.add(TrainingItsMaxTextBox);

            label = new JLabel("Training Iterations Incr:");
            TrainingItsIncTextBox = new JTextField("100", 10);
            label.setLabelFor(TrainingItsIncTextBox);
            temp.add(label);
            temp.add(TrainingItsIncTextBox);
        }
        contentPane.add(temp);


        temp = new JPanel();
        JLabel EpochLabel = new JLabel("Training Epochs:");
        EpochTextBox = new JTextField("10", 6);
        EpochLabel.setLabelFor(EpochTextBox);
        temp.add(EpochLabel);
        temp.add(EpochTextBox);
        contentPane.add(temp);

        temp = new JPanel();
        final JButton RunExpButton = new JButton("Run Learning Rate vs Momentum Experiment");
        RunExpButton.addActionListener(e -> {
            new Thread(() -> {
                double[] learningRates = getRange(NMinTextBox.getText(),NMaxTextBox.getText(),NIncTextBox.getText());
                double[] alphas = getRange(AlphaMinTextBox.getText(),AlphaMaxTextBox.getText(),AlphaIncTextBox.getText());

                Map<String,Map<String,List<Double>>> results = new HashMap<>();

                int trainingIterations=0, epochs=0;

                for(double learningRate : learningRates ) {
                    results.put(learningRate+"",new HashMap<>());

                    for (double alpha : alphas) {
                        results.get(learningRate + "").put(alpha + "",new ArrayList<>());

                        for (int i = 0; i < 20; i++) {
                            final NNExperimentParams nnExpParams = getNNExpParams();

                            trainingIterations = nnExpParams.getTrainingIterationsPerEpoch();
                            epochs = nnExpParams.getEpochs();

                            nnExpParams.setN(learningRate);
                            nnExpParams.setAlpha(alpha);
                            SBPNNExperiment sbpnnExperiment = new SBPNNExperiment(nnExpParams);
                            sbpnnExperiment.init();

                            final SBPResults runResults = sbpnnExperiment.run();
                            double error = runResults.networkError;

                            results.get(learningRate+"").get(alpha+"").add(error);

                            System.out.println("N="+learningRate+" Alpha=" + alpha +" Error=" + error);
                        }
                    }
                }

                String[] learningRates_ = convertToStringArray(learningRates);
                String[] alphas_ = convertToStringArray(alphas);


                Map<String,Map<String,Double>> averagedResults = averageResults(results);

                System.out.println("Writing results to file!");
                XORResultsWriter.writeExperimentResults("LearningRateVsMomentum.csv","TIs:"+trainingIterations+" epochs:" + epochs,"Learning Rate","Momentum Alpha",learningRates_,alphas_,averagedResults);
                System.out.println("Done");

            }).start();
        });
        temp.add(RunExpButton);


        final JButton RunLearningTrainItersExpButton = new JButton("Run Learning Rate vs Training Iterations Experiment");
        RunLearningTrainItersExpButton.addActionListener(e -> {
            new Thread(() -> {
                double[] learningRates = getRange(NMinTextBox.getText(),NMaxTextBox.getText(),NIncTextBox.getText());
                int[] trainingIterations = getIntRange(TrainingItsMinTextBox.getText(),TrainingItsMaxTextBox.getText(),TrainingItsIncTextBox.getText());

                Map<String,Map<String,List<Double>>> results = new HashMap<>();


                double alpha=0;
                int epochs=0;

                for(double learningRate : learningRates ) {
                    results.put(learningRate+"",new HashMap<>());

                    for (int trainingIters : trainingIterations) {
                        results.get(learningRate + "").put(trainingIters + "",new ArrayList<>());

                        for (int i = 0; i < 20; i++) {
                            final NNExperimentParams nnExpParams = getNNExpParams();

                            alpha = nnExpParams.getAlpha();
                            epochs = nnExpParams.getEpochs();

                            nnExpParams.setN(learningRate);
                            nnExpParams.setTrainingIterationsPerEpoch(trainingIters);
                            SBPNNExperiment sbpnnExperiment = new SBPNNExperiment(nnExpParams);
                            sbpnnExperiment.init();

                            final SBPResults runResults = sbpnnExperiment.run();
                            double error = runResults.networkError;

                            results.get(learningRate + "").get(trainingIters + "").add(error);
                            System.out.println("N=" + learningRate + " Training Iterations=" + trainingIters + " Error=" + error);
                        }
                    }
                }

                String[] trainingIters = convertToStringArray(trainingIterations);
                String[] learningRates_ = convertToStringArray(learningRates);

                Map<String,Map<String,Double>> averagedResults = averageResults(results);

                System.out.println("Writing results to file!");
                XORResultsWriter.writeExperimentResults("LearningRateVsTrainingIters.csv","Alpha:" +alpha + " Epochs:" +epochs ,"Learning Rate","Training Iterations",learningRates_,trainingIters,averagedResults);
                System.out.println("Done");

            }).start();
        });
        temp.add(RunLearningTrainItersExpButton);


        final JButton RunMomentumVsItersExpButton = new JButton("Run Momentum vs Training Iterations Experiment");
        RunMomentumVsItersExpButton.addActionListener(e -> {
            new Thread(() -> {
                int[] trainingIterations = getIntRange(TrainingItsMinTextBox.getText(),TrainingItsMaxTextBox.getText(),TrainingItsIncTextBox.getText());
                double[] alphas = getRange(AlphaMinTextBox.getText(),AlphaMaxTextBox.getText(),AlphaIncTextBox.getText());

                Map<String,Map<String,List<Double>>> results = new HashMap<>();

                double N=0;
                int epochs=0;

                for (double alpha : alphas) {
                    results.put(alpha+"",new HashMap<>());

                    for(int trainingIters : trainingIterations ) {
                        results.get(alpha + "").put(trainingIters + "",new ArrayList<>());

                        for (int i = 0; i < 20; i++) {
                            final NNExperimentParams nnExpParams = getNNExpParams();

                            N = nnExpParams.getN();
                            epochs = nnExpParams.getEpochs();

                            nnExpParams.setTrainingIterationsPerEpoch(trainingIters);
                            nnExpParams.setAlpha(alpha);
                            SBPNNExperiment sbpnnExperiment = new SBPNNExperiment(nnExpParams);
                            sbpnnExperiment.init();

                            final SBPResults runResults = sbpnnExperiment.run();
                            double error = runResults.networkError;

                            results.get(alpha + "").get(trainingIters + "").add(error);

                            System.out.println("Alpha=" + alpha + " TrainingIters= " + trainingIters + " Error=" + error);
                        }
                    }
                }

                String[] trainingIters = convertToStringArray(trainingIterations);
                String[] alphas_ = convertToStringArray(alphas);

                Map<String,Map<String,Double>> averagedResults = averageResults(results);

                System.out.println("Writing results to file!");
                XORResultsWriter.writeExperimentResults("MomentumVsTrainingIters.csv","N:" +N + " Epochs:" +epochs,"Momentum Alpha","Training Iterations",alphas_,trainingIters,averagedResults);
                System.out.println("Done");

            }).start();
        });
        temp.add(RunMomentumVsItersExpButton);
        contentPane.add(temp);

        setContentPane(contentPane);
        setSize(800,800);
        setVisible(true);
    }


    private Map<String,Map<String,Double>> averageResults(Map<String, Map<String, List<Double>>> results) {
        Map<String,Map<String,Double>> averagedResults = new HashMap<>();

        for(String attr1 : results.keySet() ){
            Map<String, List<Double>> attr1Map = results.get(attr1);


            Map<String,Double> newAttr1Map = new HashMap<>();
            averagedResults.put(attr1,newAttr1Map);


            for(String attr2 : attr1Map.keySet() ){
                List<Double> runErrors = attr1Map.get(attr2);

                double avg =0;
                for(double error : runErrors)
                    avg += error;

                avg /= runErrors.size();

                newAttr1Map.put(attr2,avg);
            }
        }

        return averagedResults;
    }


    public static String[] convertToStringArray(int[] array){
        String[] trainingIters = new String[array.length];
        for (int i = 0; i < array.length; i++)
            trainingIters[i] = array[i]+"";
        return trainingIters;
    }

    public static String[] convertToStringArray(double[] array){
        String[] trainingIters = new String[array.length];
        for (int i = 0; i < array.length; i++)
            trainingIters[i] = array[i]+"";
        return trainingIters;
    }

    public NNExperimentParams getNNExpParams(){
        double A = 1.716;
        double B = 0.6667;
        double N = Double.parseDouble(NMinTextBox.getText());
        int hiddenLayers = 1;
        int trainingIterationsPerEpoch = Integer.parseInt(TrainingItsMinTextBox.getText());
        int epochs = Integer.parseInt(EpochTextBox.getText());
        double desiredErrorRate = 0.00001;
        double alpha = Double.parseDouble(AlphaMinTextBox.getText());

        return new NNExperimentParams(A, B, N, true, epochs, trainingIterationsPerEpoch, 2, hiddenLayers, desiredErrorRate, alpha, 2);
    }

    private int[] getIntRange(String MinTextBox, String MaxTextBox, String IncTextBox) {

        int inc = Integer.parseInt(IncTextBox);
        int min = Integer.parseInt(MinTextBox);
        int max = Integer.parseInt(MaxTextBox);

        int[] range = new int[(max-min)/inc];

        int c=0;
        for (int i = min; c < range.length; i+=inc, c++)
            range[c] = i;

        return range;
    }


    private double[] getRange(String MinTextBox, String MaxTextBox, String IncTextBox) {

        double inc = Double.parseDouble(IncTextBox);
        double min = Double.parseDouble(MinTextBox);
        double max = Double.parseDouble(MaxTextBox);

        double[] range = new double[(int)((max-min)/inc)];

        int c=0;
        for (double i = min; c < range.length; i+=inc, c++)
            range[c] = i;

        return range;
    }


    public double[] getRange(String text) {

        String[] rangeNIncSplit = text.split(":");
        String[] rangeSplit = rangeNIncSplit[0].split("-");
        double inc = Double.parseDouble(rangeNIncSplit[1]);
        double min = Double.parseDouble(rangeSplit[0]);
        double max = Double.parseDouble(rangeSplit[1]);

        double[] range = new double[(int)((max-min)/inc)];

        int c=0;
        for (double i = min; c < range.length; i+=inc, c++)
            range[c] = i;

        return range;
    }

}
