package main;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import neuralnet.NNTrainingDataLoader;
import tests.NNTestMain;
import tests.TestMain;
import training.TrainingDataGenerator;
import ui.*;
import util.WTFException;

import javax.swing.*;
import java.util.List;


/**
 *
 * Created by Chris on 1/27/2016.
 */
public class Main {
    private static NeuralNetExperimentPanel nnPanel;

    public static void main(String[] args) {
        for (String s : args){
            if( "-gentrainingdata".equals(s.toLowerCase())){
                TrainingDataGenerator.main(args);
                System.exit(0);
            }
            else if("-unittest".equals(s.toLowerCase())){
                TestMain.main(System.out::println);
                System.exit(1);
            }
            else if("-unittestnn".equals(s.toLowerCase())){
                NNTestMain.main(System.out::println);
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



            JMenu unitTesting = new JMenu("Unit Testing");

            JMenuItem unitTest = new JMenuItem("Unit Test Rubix Cube",'r');
            unitTest.addActionListener(e -> new UnitTestRubixCubeWindow());
            unitTesting.add(unitTest);

            JMenuItem unitTestNN = new JMenuItem("Unit Test Neural Net",'n');
            unitTestNN.addActionListener(e -> new UnitTestSBPWindow());
            unitTesting.add(unitTestNN);

            JMenuItem unitTestGeneticAlg = new JMenuItem("Unit Test Genetic Algorithm",'g');
            unitTestGeneticAlg.addActionListener(e -> new UnitTestGeneticsWindow());
            unitTesting.add(unitTestGeneticAlg);

            menuBar.add(unitTesting);

            mainFrame.setJMenuBar(menuBar);




            nnPanel = new NeuralNetExperimentPanel();

            JTabbedPane tpane = new JTabbedPane();
            tpane.add("Genetic Xor Solution" , new GeneticXorSolutionPanel());
            tpane.add("Search-NN-Solve Experiment",new SearchToNNToSolveExperimentPanel());
            tpane.add("NN-Variations Experiment",new Phase3ExperimentPanel());
            tpane.add("Neural Net", nnPanel);

            tpane.add("Rubix Cube",new RubixCubePanel(Main::cubeNNInterface));


            mainFrame.setContentPane(tpane);
            mainFrame.setSize(1600,1000);
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        });
    }


    public static String cubeNNInterface(int cubesize, String cubeState){
        String encodedCubeState = TrainingDataGenerator.getCubeStateTT(cubeState);
        List<Double> inputs = NNTrainingDataLoader.toDoubleList(encodedCubeState);
        List<Double> outputs = nnPanel.feedForward(inputs);

        return getMoveFromNNOutput(cubesize,outputs);
    }

    public static String getMoveFromNNOutput(int cubeSize, List<Double> outputs){

        String sliceEncoding = "";
        int o;
        int i=0;
        for (; i < 3; i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            sliceEncoding += o + ",";
        }

        String colOrRow = "";
        for (; i < 3+cubeSize; i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            colOrRow += o + ",";
        }

        String dir = "";
        for (; i < outputs.size(); i++) {
            if( outputs.get(i) > 0 )
                o = 1;
            else o = -1;
            dir += o + ",";
        }

        int colOrRowInt = TrainingDataGenerator.getColOrRowdecoding(colOrRow);
        dir = TrainingDataGenerator.getDirdecoding(dir);
        sliceEncoding = TrainingDataGenerator.decodeSlice(sliceEncoding);

        if( colOrRowInt == -1)
            throw new WTFException();
        return sliceEncoding + ":"  + colOrRowInt + ":" + dir ;
    }

}










