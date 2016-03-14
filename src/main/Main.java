package main;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import tests.NNTestMain;
import tests.TestMain;
import training.TrainingDataGenerator;
import ui.Display;
import ui.ExperimentPopupWindow;

import javax.swing.*;

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
            Display.main(args);
            new ExperimentPopupWindow();
        });


    }
}
