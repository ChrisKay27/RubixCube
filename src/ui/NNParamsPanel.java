package ui;

import neuralnet.*;
import org.jetbrains.annotations.Nullable;
import sbp.SBP;
import sbp.SBPNNExperiment;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.function.Function;

/**
 * Created by chris_000 on 2/24/2016.
 */
public class NNParamsPanel extends JPanel{



    private final JTextField ATextBox;
    private final JTextField BTextBox;
    private final JTextField ErrorRateTextBox;
    private final JTextField NTextBox;
    private final JTextField AlphaTextBox;
    private final JTextField TrainingItsTextBox;
    private final TextBox topologyTextBox;
    private final JTextField EpochTextBox;
    private final JTextField weightDecayTextBox;
    private final JLabel currentNNLabel;
    private final JLabel trainTuplesLabel;
    private final JButton run;
    private final JButton feedForwardButton;

    private long nextUpdate;

    private boolean useBias = true;
    private List<TrainingTuple> trainingTuples;
    private boolean updateLabel = true;

    private @Nullable NeuralNet nnToUse;

    private SBPNNExperiment sbpNNExperiment;

    public NNParamsPanel() {
        super(new GridLayout(2,11));

        setBorder(BorderFactory.createTitledBorder("Neural Network Panel"));

        JPanel temp = new JPanel();
        JPanel temp2 = new JPanel();
        JLabel ALabel = new JLabel("A:");
        ATextBox = new JTextField("1",4);
        ALabel.setLabelFor(ATextBox);
        temp2.add(ALabel);
        temp2.add(ATextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel BLabel = new JLabel("B:");
        BTextBox = new JTextField("0.3",4);
        BLabel.setLabelFor(BTextBox);
        temp2.add(BLabel);
        temp2.add(BTextBox);
        temp.add(temp2);
        add(temp);


        temp = new JPanel();
        temp2 = new JPanel();
        JLabel NLabel = new JLabel("Learning Rate:");
        NTextBox = new JTextField("0.125",8);
        NLabel.setLabelFor(NTextBox);
        temp2.add(NLabel);
        temp2.add(NTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel alphaLabel = new JLabel("Momentum Alpha:");
        AlphaTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(AlphaTextBox);
        temp2.add(alphaLabel);
        temp2.add(AlphaTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel weightDecayLabel = new JLabel("Weight Decay:");
        weightDecayTextBox = new JTextField("0.01",5);
        alphaLabel.setLabelFor(weightDecayTextBox);
        temp2.add(weightDecayLabel);
        temp2.add(weightDecayTextBox);
        temp.add(temp2);
        add(temp);

        temp = new JPanel();
        temp2 = new JPanel();
        JLabel TrainingItsLabel = new JLabel("Training Iterations:");
        TrainingItsTextBox = new JTextField("1000",10);
        TrainingItsLabel.setLabelFor(TrainingItsTextBox);
        temp2.add(TrainingItsLabel);
        temp2.add(TrainingItsTextBox);
        temp.add(temp2);

        temp2 = new JPanel();
        JLabel EpochLabel = new JLabel("Training Epochs:");
        EpochTextBox = new JTextField("100",6);
        EpochLabel.setLabelFor(EpochTextBox);
        temp2.add(EpochLabel);
        temp2.add(EpochTextBox);
        temp.add(temp2);
        add(temp);

        temp = new JPanel();
        temp2 = new JPanel();
        JCheckBox biasCheckbox = new JCheckBox("Use Bias Neuron");
        biasCheckbox.setSelected(true);
        biasCheckbox.addActionListener(e2 -> useBias = !useBias);
        temp2.add(biasCheckbox);
        temp.add(temp2);



        temp2 = new JPanel();
        topologyTextBox = new TextBox("Topology","324-2-7",20);
        temp2.add(topologyTextBox);
        temp.add(temp2);
        add(temp);


        temp2 = new JPanel();
        temp = new JPanel();
        JLabel ErrorRateLabel = new JLabel("Error Rate:");
        ErrorRateTextBox = new JTextField("0.000001",20);
        ErrorRateLabel.setLabelFor(ErrorRateTextBox);
        temp2.add(ErrorRateLabel);
        temp2.add(ErrorRateTextBox);
        temp.add(temp2);
        add(temp);

        final JButton loadNNButton = new JButton("Load NN");
        final JButton saveNNButton = new JButton("Save NN");

        JLabel infoLabel = new JLabel("Status: Not started");



        temp = new JPanel();
        final JButton stop = new JButton("Stop");
        run = new JButton("Run");
        final JButton clear = new JButton("Clear Loaded NN");

        trainTuplesLabel = new JLabel("No training data loaded");
        temp.add(trainTuplesLabel);

        JFileChooser fc = new JFileChooser();
        final JButton selectTrainingData = new JButton("Select Training Data");
        selectTrainingData.addActionListener(e1 -> {
            int returnVal = fc.showOpenDialog(NNParamsPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                trainingTuples = NNTrainingDataLoader.loadTrainingTuples(file);
                if( trainingTuples != null )
                    if( !trainingTuples.isEmpty() ) {
                        run.setEnabled(true);
                        trainTuplesLabel.setText(trainingTuples.size() + " training tuples loaded");

                        int inputs = trainingTuples.get(0).getInputs().size();
                        int outputs = trainingTuples.get(0).getExpectedOutputs().size();
                        String t = topologyTextBox.getText();
                        t = t.substring(t.indexOf('-'),t.lastIndexOf('-')+1);
                        topologyTextBox.setText(inputs+t+outputs);

                        if( nnToUse != null && (nnToUse.getInputNeurons().size() != trainingTuples.get(0).getInputs().size()
                                || nnToUse.getOutputNeurons().size() != trainingTuples.get(0).getExpectedOutputs().size())){
                            clear.doClick();
                            infoLabel.setText("Loading training tuples did not match NN input or output!");
                        }
                    }
            }
        });
        temp.add(selectTrainingData);


        run.setEnabled(false);
        run.addActionListener(e -> new Thread(()->{

            NNExperimentParams nnExpParams = getNNExpParams();
            if(trainingTuples.get(0).getInputs().size() != nnExpParams.getInputs()) {
                infoLabel.setText("<html>Loaded training tuples do not have the<br> same number of inputs as the nn!");
                return;
            }
            if(trainingTuples.get(0).getExpectedOutputs().size() != nnExpParams.getOutputs()) {
                infoLabel.setText("<html>Loaded training tuples do not have the<br> same number of outputs as the nn!");
                return;
            }

            run.setEnabled(false);
            stop.setEnabled(true);
            loadNNButton.setEnabled(false);
            saveNNButton.setEnabled(false);
            clear.setEnabled(false);


            sbpNNExperiment = new SBPNNExperiment(nnExpParams);
            sbpNNExperiment.setTrainingTuples(trainingTuples);

            if( nnToUse != null )
                sbpNNExperiment.setNeuralNet(nnToUse);
            else
                sbpNNExperiment.init();

            nnToUse = sbpNNExperiment.getNeuralNet();
            nnToUse.setNetworkError(Double.MAX_VALUE);
            clear.setVisible(true);

            sbpNNExperiment.setUpdateListener(sbpState -> {
                if( !updateLabel ) return;
                SwingUtilities.invokeLater(()-> {
                    if( nextUpdate > System.currentTimeMillis()) return;
                    nextUpdate = System.currentTimeMillis() + 100;
                    infoLabel.setText(String.format("<html>Status: Epoch: %d Iteration: %d<br> Error: %f<br>Over %d tuples<br>%f Error per tuple (avg)",
                            sbpState.epoch, sbpState.iteration, sbpState.bestError,trainingTuples.size(),sbpState.bestError/trainingTuples.size()));
                });
            });

            System.out.println("Running Experiment!");
            final SBP.SBPResults runResults = sbpNNExperiment.run();
            nnToUse = (NeuralNet) runResults.sbpImpl;

            double error = runResults.networkError;
            System.out.println("Experiment over!");
            System.out.println("Iterations: " + runResults.numberOfIterationsTaken + " Epochs: " + runResults.numberOfEpochs + " Error: " + error);


            SwingUtilities.invokeLater(stop::doClick);

        }).start());
        temp.add(run);

        stop.addActionListener(e -> {
            stop.setEnabled(false);
            sbpNNExperiment.stop();
            run.setEnabled(true);
            loadNNButton.setEnabled(true);
            saveNNButton.setEnabled(true);
            clear.setEnabled(true);
        });
        stop.setEnabled(false);


        temp.add(stop);



        currentNNLabel = new JLabel("Current NN: None");
        clear.addActionListener(e -> {
            stop.doClick();
            nnToUse = null;
            clear.setVisible(false);
            currentNNLabel.setText("Current NN: None");
            infoLabel.setText("Status: Cleared Loaded NN");
        });
        clear.setVisible(false);
        temp.add(clear);

        add(temp);



        temp = new JPanel();
        temp.add(currentNNLabel);

        fc.setCurrentDirectory(new File("."));

        loadNNButton.addActionListener(e -> {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                infoLabel.setText("<html>Status: Loading NN");

                File file = fc.getSelectedFile();

                nnToUse = NeuralNetIO.loadNN(file);

                if( nnToUse == null ){
                    infoLabel.setText("<html>Status: Unable to load NN from<br>" + file.getName());
                    currentNNLabel.setText("Current NN: None");
                }
                else {
                    clear.setVisible(true);
                    infoLabel.setText("<html>Status: Loaded NN");
                    currentNNLabel.setText("Current NN: "+ nnToUse.getTopology());

                    int inputs = nnToUse.getInputNeurons().size();
                    int outputs = nnToUse.getOutputNeurons().size();


                    StringBuilder sb = new StringBuilder();
                    nnToUse.getHiddenLayers().forEach(hiddenLayer -> sb.append(hiddenLayer.size()).append('-'));
                    String hnpl = sb.toString();
                    if(hnpl.lastIndexOf("-") == hnpl.length()-1)
                        hnpl = hnpl.substring(0,hnpl.length()-1);

                    topologyTextBox.setText(inputs+"-"+hnpl+"-"+outputs);


                    biasCheckbox.setSelected(nnToUse.getBiasNeuron()!=null);


                    Function<Double,Double> sigmoid = net -> getA()*Math.tanh(getB()*net);

                    nnToUse.getNNParams().setSigmoid(sigmoid);

                }
            }
        });
        temp.add(loadNNButton);



        saveNNButton.addActionListener(e -> {
            int returnVal = fc.showSaveDialog(NNParamsPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                if( sbpNNExperiment == null ){
                    infoLabel.setText("<html>You must start a run<br> to create the specified NN");
                }
                else {
                    NeuralNetIO.saveNN(sbpNNExperiment.getNeuralNet(), file);
                    infoLabel.setText("<html>Status: NN Saved to:<br> " + file.getName());
                }
            }
        });
        temp.add(saveNNButton);

        feedForwardButton = new JButton("Feed Forward");
        feedForwardButton.addActionListener(e -> new FeedForwardWindow(nnToUse,trainingTuples));
        temp.add(feedForwardButton);
        add(temp);


        temp = new JPanel();
        JCheckBox updateCB = new JCheckBox("Update this label");
        updateCB.setSelected(true);
        updateCB.addActionListener(e->updateLabel = !updateLabel);
        temp.add(updateCB);
        temp.add(infoLabel);
        add(temp);

    }

    public NNExperimentParams getNNExpParams(){
        double A = Double.parseDouble(ATextBox.getText());
        double B = Double.parseDouble(BTextBox.getText());
        double N = Double.parseDouble(NTextBox.getText());

        int trainingIterationsPerEpoch = Integer.parseInt(TrainingItsTextBox.getText());
        int epochs = Integer.parseInt(EpochTextBox.getText());
        double desiredErrorRate = Double.parseDouble(ErrorRateTextBox.getText());
        double alpha = Double.parseDouble(AlphaTextBox.getText());


        String[] hNeuronsSpl = topologyTextBox.getText().split("-");
        int[] hNeurons = new int[hNeuronsSpl.length-2];
        int c = 0;
        for (int i = 1; i < hNeuronsSpl.length-1; i++) {
            String num = hNeuronsSpl[i];
            hNeurons[c++] = Integer.parseInt(num);
        }

        int hiddenLayers = hNeurons.length;

        NNExperimentParams nnExperimentParams = new NNExperimentParams(A, B, N, useBias, epochs, trainingIterationsPerEpoch, getInputs(), hiddenLayers, getOutputs(), desiredErrorRate, alpha, hNeurons);
        nnExperimentParams.setWeightDecay(getWeightDecay());
        return nnExperimentParams;
    }

    public int getEpochs() {
        return Integer.parseInt(EpochTextBox.getText());
    }

    public int getTrainingIterations() {
        return Integer.parseInt(TrainingItsTextBox.getText());
    }

    public double getA() {
        return Double.parseDouble(ATextBox.getText());
    }

    public double getB() {
        return Double.parseDouble(BTextBox.getText());
    }
    public double getN() {
        return Double.parseDouble(NTextBox.getText());
    }
    public double getAlpha() {
        return Double.parseDouble(AlphaTextBox.getText());
    }

    public double getHiddenLayers() {
        return topologyTextBox.getText().split("-").length;
    }

    public double getErrorRate() {
        return Double.parseDouble(ErrorRateTextBox.getText());
    }

    public int getInputs() {
        return Integer.parseInt(topologyTextBox.getText().split("-")[0]);
    }
    public int getOutputs() {
        String[] split = topologyTextBox.getText().split("-");
        return Integer.parseInt(split[split.length-1]);
    }
    public double getWeightDecay() {
        return Double.parseDouble(weightDecayTextBox.getText());
    }

    public int[] getHiddenNeurons(){
        String[] hNeuronsSpl = topologyTextBox.getText().split("-");
        int[] hNeurons = new int[hNeuronsSpl.length];
        for (int i = 0; i < hNeuronsSpl.length; i++) {
            String num = hNeuronsSpl[i];
            hNeurons[i] = Integer.parseInt(num);
        }
        return hNeurons;
    }

    public void setTrainingTuples(List<TrainingTuple> trainingTuples) {
        this.trainingTuples = trainingTuples;
        trainTuplesLabel.setText(trainingTuples.size() + " training tuples loaded");
        run.setEnabled(true);
    }

    public List<Double> feedForward(List<Double> inputs) {
        return nnToUse.feedForward(inputs);
    }
}
