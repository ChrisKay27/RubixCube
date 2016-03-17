package main;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import neuralnet.NNTrainingDataLoader;
import tests.NNTestMain;
import tests.TestMain;
import training.TrainingDataGenerator;
import ui.NNExperimentPopupWindow;
import ui.NeuralNetPanel;
import ui.RubixCubePanel;
import ui.SearchToNNToSolveExperimentPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 *
 * Created by Chris on 1/27/2016.
 */
public class Main {
    private static NeuralNetPanel nnPanel;

    public static void main(String[] args) {
        for (String s : args){
            if( "-gentrainingdata".equals(s.toLowerCase())){
                TrainingDataGenerator.main(args);
                System.exit(0);
            }
            else if("-unittest".equals(s.toLowerCase())){
                TestMain.main(args);
                System.exit(1);
            }
            else if("-unittestnn".equals(s.toLowerCase())){
                NNTestMain.main(args);
                System.exit(1);
            }
        }

        SwingUtilities.invokeLater(()->{
            try {
                UIManager.removeAuxiliaryLookAndFeel(UIManager.getLookAndFeel());

                UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            JFrame mainFrame = new JFrame("CPSC 371 AI - Chris Kaebe");

            //Setup menu bar
            JMenuBar menuBar = new JMenuBar();

            JMenu experimentMenu = new JMenu("Experiments");

            JMenuItem NNexperimentMenu = new JMenuItem("NN Heat Map Experiment",'n');
            NNexperimentMenu.addActionListener(e -> new NNExperimentPopupWindow());
            experimentMenu.add(NNexperimentMenu);
            menuBar.add(experimentMenu);

//            JMenu utilMenu = new JMenu("Util");
//            JMenuItem cubeNNInterface = new JMenuItem("Cube NN Interface",'e');
//            cubeNNInterface.addActionListener(e -> new CubeNNInterface(RubixCubePanel.this));
//            utilMenu.add(cubeNNInterface);
//            menuBar.add(utilMenu);

            mainFrame.setJMenuBar(menuBar);

            nnPanel = new NeuralNetPanel();

            JTabbedPane tpane = new JTabbedPane();
            tpane.add("Search-NN-Solve Experiment",new SearchToNNToSolveExperimentPanel());
            tpane.add("Neural Net", nnPanel);


            tpane.add("Rubix Cube",new RubixCubePanel(Main::cubeNNInterface));


            mainFrame.setContentPane(tpane);

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            mainFrame.setSize(width-100,height-100);
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            mainFrame.setExtendedState(Frame.ICONIFIED);
            mainFrame.setVisible(true);
        });
    }


    public static String cubeNNInterface(String cubeState){
        String encodedCubeState = TrainingDataGenerator.getCubeStateTT(cubeState);
        List<Double> inputs = NNTrainingDataLoader.toDoubleList(encodedCubeState);
        List<Double> outputs = nnPanel.feedForward(inputs);

        return getMoveFromNNOutput(outputs);
    }

    public static String getMoveFromNNOutput(List<Double> outputs){

        String sliceEncoding = "";
        int o;
        for (int i = 0; i < 3; i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            sliceEncoding += o + ",";
        }

        String colOrRow = "";
        for (int i = 3; i < 6; i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            colOrRow += o + ",";
        }

        String dir = "";
        for (int i = 6; i < 7; i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            dir += o + ",";
        }

        int colOrRowInt = TrainingDataGenerator.getColOrRowdecoding(colOrRow);
        dir = TrainingDataGenerator.getDirdecoding(dir);
        sliceEncoding = TrainingDataGenerator.decodeSlice(sliceEncoding);

        return sliceEncoding + ":"  + colOrRowInt + ":" + dir ;
    }

}










