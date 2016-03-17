package ui;

import training.TrainingDataGenerator;

import javax.swing.*;

/**
 * Created by Chris on 3/7/2016.
 */
public class CubeNNInterface extends JFrame {
    public CubeNNInterface(NeuralNetPanel nnPanel, RubixCubePanel display) {

        String inputs = TrainingDataGenerator.getCubeStateTT(display.getDisplayedCube().toString());

        nnPanel.getTTField().setText(inputs);
        nnPanel.getFFButton().doClick();
        String output = nnPanel.getOutputLabel().getText().replace("Output: ", "");
        System.out.println("output: " + output);
        output = output.replaceAll("2","1");
//        output = output.replaceAll("-1","0");
//        output = new StringBuilder(output).reverse().toString().replaceAll("0","-1");
        String[] outSpl = output.split(",");

        String sliceEncoding = "";
        for (int i = 0; i < 3; i++) {
            sliceEncoding += outSpl[i] + ",";
        }

        String colOrRow = "";
        for (int i = 3; i < 6; i++) {
            colOrRow += outSpl[i] + ",";
        }

        String dir = "";
        for (int i = 6; i < 7; i++) {
            dir += outSpl[i] + ",";
        }
        System.out.println("after: " + sliceEncoding+" : "+colOrRow+" : "+dir);

        int colOrRowInt = TrainingDataGenerator.getColOrRowdecoding(colOrRow);
        dir = TrainingDataGenerator.getDirdecoding(dir);
        sliceEncoding = TrainingDataGenerator.decodeSlice(sliceEncoding);

        JLabel l = new JLabel(sliceEncoding +":" + dir +":" + colOrRowInt);
        add(l);

        setSize(300,300);
        setVisible(true);
    }


    public static void main(String[] args) {

        String[] outputs = new String[]{"-1,1,-1,1,-1,-1,1","-1,-1,1,1,-1,-1,-1","-1,-1,1,-1,1,-1,1","1,-1,-1,-1,1,-1,-1"
                ,"-1,-1,1,1,-1,-1,-1","-1,1,-1,1,-1,-1,1","1,-1,-1,1,-1,-1,1","-1,-1,1,1,-1,-1,1","-1,-1,1,-1,-1,1,1"};

        for(String output : outputs ) {

            String[] outSpl = output.split(",");

            String sliceEncoding = "";
            for (int i = 0; i < 3; i++) {
                sliceEncoding += outSpl[i] + ",";
            }

            String colOrRow = "";
            for (int i = 3; i < 6; i++) {
                colOrRow += outSpl[i] + ",";
            }

            String dir = "";
            for (int i = 6; i < 7; i++) {
                dir += outSpl[i] + ",";
            }


            int colOrRowInt = TrainingDataGenerator.getColOrRowdecoding(colOrRow);
            dir = TrainingDataGenerator.getDirdecoding(dir);
            sliceEncoding = TrainingDataGenerator.decodeSlice(sliceEncoding);

            System.out.println(sliceEncoding + ":" + dir + ":" + colOrRowInt);
        }
    }

}
