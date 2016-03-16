package main;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import tests.NNTestMain;
import tests.TestMain;
import training.TrainingDataGenerator;
import ui.*;

import javax.swing.*;
import java.awt.*;

/**
 *
 * Created by Chris on 1/27/2016.
 */
public class Main {
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


            JTabbedPane tpane = new JTabbedPane();
            tpane.add("Search-NN-Solve Experiment",new SearchToNNToSolveExperimentPanel());
            tpane.add("Neural Net", new NeuralNetPanel());


            tpane.add("Rubix Cube",new RubixCubePanel());


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
}
