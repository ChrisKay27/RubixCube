package main;

import tests.TestMain;
import training.TrainingDataGenerator;
import ui.Display;

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
        }
        Display.main(args);
    }
}
