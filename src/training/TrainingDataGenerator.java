package training;

import util.WTFException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class takes the results produced by running an experiment and combines them, in encoded form and without duplicates, into one file.
 * Created by Chris on 1/25/2016.
 */
public class TrainingDataGenerator {

    public static final String NULL = "null";

    private final int cubeSize;
    private final String inputFolderPath;
    private final String outputFilePath;

    public TrainingDataGenerator(int cubeSize, String inputFolderPath, String outputFilePath) {
        this.cubeSize = cubeSize;
        this.inputFolderPath = inputFolderPath;
        this.outputFilePath = outputFilePath;
    }


    public static void main(String[] args)  {
        String inputFolderPath = "results"; //C:\Users\chris_000\SkyDrive\Documents\School\CPSC 371 AI\RubixCube\
        String outputFilePath = "results\\trainingData.txt"; //C:\Users\chris_000\SkyDrive\Documents\School\CPSC 371 AI\RubixCube\
        new TrainingDataGenerator(3,inputFolderPath,outputFilePath).generateTrainingData();
      
    }

    public void generateTrainingData() {

        HashMap<String,String> stateToMove = getCubeStatesToMoves();

        List<String> encodedCubeStatesAndMoves = encodeCubeStateAndMoves(stateToMove);

        writeEncodedCubeStatesAndMovesToFile(encodedCubeStatesAndMoves);

        System.out.println("Wrote " + encodedCubeStatesAndMoves.size() + " lines to results/trainingData.txt");
    }



    private HashMap<String,String> getCubeStatesToMoves(){

        File inputFolder = new File(inputFolderPath);
        if(!inputFolder.exists()) {
            System.err.println("Results folder doesn't exist in the folder of the jar! Try running experiments first.");
            System.exit(69);
        }

        File[] resultFiles = inputFolder.listFiles();
        if(resultFiles == null ) {
            System.err.println("No results files to load! (on path: " + inputFolderPath);
            System.exit(666);
        }

        HashMap<String, String> stateToMove = new HashMap<>();

        ArrayList<String> lines = new ArrayList<>();

        String size = null;
        for (File resultFile : resultFiles) {

            if (!resultFile.getName().endsWith(".txt") || resultFile.getName().equals("trainingData.txt"))
                continue;

            if( size != null ){
                if( !resultFile.getName().startsWith(size) )
                    continue;
            }
            else{
                size = resultFile.getName().charAt(0)+"";
            }


            try (BufferedReader br = new BufferedReader(new FileReader(resultFile))) {

                lines.clear();

                br.lines().forEach(lines::add);

                for (String s : lines) {
                    s = s.substring(1, s.length() - 1);

                    int lastIndexOfComma = s.lastIndexOf(',');
                    String cubeState = s.substring(0, lastIndexOfComma);
                    String move = s.substring(lastIndexOfComma + 1);


                    if(NULL.equals(move))
                        continue;

                    if (!stateToMove.containsKey(cubeState)) {
                        stateToMove.put(cubeState, move);
                    }

                    //System.out.println(cubeState + "  " + move);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stateToMove;
    }


    public static String getCubeStateTT(String cubeState){
        String encodedCubeState = cubeState.replaceAll(",", "").replaceAll("\\[", "").replaceAll("]", "").replace("Cube;Front:", "").replace("Right:", "").replace("Back:", "").replace("Left:", "").replace("Top:", "").replace("Bottom:", "");
        //System.out.println(cubeState);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < encodedCubeState.length(); i++) {
            sb.append(getColorEncoding(encodedCubeState.charAt(i)));
        }
        encodedCubeState = sb.toString();
        encodedCubeState = encodedCubeState.substring(0, encodedCubeState.length() - 1);
        return encodedCubeState;
    }


    private List<String> encodeCubeStateAndMoves(HashMap<String, String> stateToMove) {
        List<String> lines = new ArrayList<>();
        for(String cubeState : stateToMove.keySet()){
            try {
                String move = stateToMove.get(cubeState);
                //System.out.println(cubeState);

                String encodedCubeState = getCubeStateTT(cubeState);


                String[] moveSplit = move.split(":");

                StringBuilder moveSb = new StringBuilder();
                moveSb.append(getSliceEncoding(moveSplit[0]));
                moveSb.append(getColOrRowEncoding(moveSplit[1],cubeSize));
                moveSb.append(getDirEncoding(moveSplit[2]));

                move = moveSb.toString().substring(0, moveSb.length() - 1);

                lines.add(encodedCubeState + "|" + move);
            }
            catch(Exception e){
                System.err.println("Exception on " + cubeState);
                e.printStackTrace();
            }
        }
        return lines;
    }



    private void writeEncodedCubeStatesAndMovesToFile(List<String> lines) {
        File trainingDataFile = new File(outputFilePath);
        if( !trainingDataFile.exists() )
            try {
                trainingDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new WTFException(e.getMessage());
            }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(trainingDataFile))){

            for( String line : lines ){
                bw.write(line);
                bw.newLine();
            }

            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WTFException(e.getMessage());
        }
    }


    /**
     *
     */
    private static String getColOrRowEncoding(String colOrRowStr, int cubeSize) {

        int colOrRow = Integer.parseInt(colOrRowStr);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cubeSize; i++) {
            if( i == colOrRow )
                sb.append("1,");
            else
                sb.append("-1,");
        }
        return sb.toString();
    }

    /**
     *
     */
    public static int getColOrRowdecoding(String colOrRowStr) {
//        System.out.println("Col or row : "+ colOrRowStr );
        colOrRowStr = colOrRowStr.replaceAll("-1","0");
        colOrRowStr = colOrRowStr.replaceAll(",","");
//        System.out.println("Col or row output : " + colOrRowStr.indexOf("1"));
        return colOrRowStr.indexOf("1");
    }


    /**
     * cw = 1,
     * ccw = -1,
     */
    private static String getDirEncoding(String s) {
        switch (s){
            case "cw": return "1,";
            case "ccw": return "-1,";
        }
        throw new WTFException("Input "+s+" has to be either Row EW or NS");
    }

    /**
     * cw = 1,
     * ccw = -1,
     */
    public static String getDirdecoding(String s) {
        switch (s){
            case "1,"  : return "cw";
            case  "-1,": return "ccw";
        }
        throw new WTFException("Input "+s+" has to be either 1 or -1");
    }


    /**
     * Row = -1,-1,1,
     * NS = -1,1,-1,
     * EW = 1,-1,-1,
     */
    private static String getSliceEncoding(String s) {
        switch (s){
            case "Row": return "-1,-1,1,";
            case "NS": return  "-1,1,-1,";
            case "EW": return  "1,-1,-1,";
        }
        throw new WTFException("Input "+s+" has to be either Row EW or NS");
    }

    public static String decodeSlice(String encodedSlice){
        switch (encodedSlice){
            case "-1,-1,1," : return "Row" ;
            case "-1,1,-1,": return "NS" ;
            case "1,-1,-1,": return "EW" ;
        }
        throw new WTFException("Input "+encodedSlice+" has to be either -1,-1,1, or " +
                "-1,1,-1, or " +
                "1,-1,-1,");
    }



    /**
     * R = -1,-1,-1,-1,-1,1,
     * O = -1,-1,-1,-1,1,-1,
     * Y = -1,-1,-1,1,-1,-1,
     * G = -1,-1,1,-1,-1,-1,
     * B = -1,1,-1,-1,-1,-1,
     * W = 1,-1,-1,-1,-1,-1,
     */
    public static String getColorEncoding(char character) {
        switch (character){
            case 'R': return "-1,-1,-1,-1,-1,1,";
            case 'O': return "-1,-1,-1,-1,1,-1,";
            case 'Y': return "-1,-1,-1,1,-1,-1,";
            case 'G': return "-1,-1,1,-1,-1,-1,";
            case 'B': return "-1,1,-1,-1,-1,-1,";
            case 'W': return "1,-1,-1,-1,-1,-1,";
        }
        throw new WTFException("Input "+character+" has to be either R O Y G B or W");
    }



    public static class TestTrainingDataGenerator{

        public static boolean test(){

            boolean pass;
            pass = testGetColorEncoding();
            pass &= testGetSliceEncoding();
            return pass;
        }




        public static boolean testGetColorEncoding(){

            boolean pass;

            try {
                pass = getColorEncoding('R').equals("-1,-1,-1,-1,-1,1,");
                pass &= getColorEncoding('O').equals("-1,-1,-1,-1,1,-1,");
                pass &= getColorEncoding('Y').equals("-1,-1,-1,1,-1,-1,");
                pass &= getColorEncoding('G').equals("-1,-1,1,-1,-1,-1,");
                pass &= getColorEncoding('B').equals("-1,1,-1,-1,-1,-1,");
                pass &= getColorEncoding('W').equals("1,-1,-1,-1,-1,-1,");
            }
            catch( WTFException wtf){
                wtf.printStackTrace();
                pass = false;
            }


            String abcs = "abcdefghijklmnopqrstuvwxyzACDEFHIJKLMNPQSTUVXZ0123456789!@#$$%^&&*(),.;'[]\\/<>?:\"{}|+_-=";

            for (int i = 0; i < abcs.length(); i++) {
                try {
                    //This should throw an exception so it never gets to pass = false;
                    getColorEncoding(abcs.charAt(i));
                    pass = false;
                }
                catch( WTFException wtf){
                    //Should get here EVERY time in this loop

                    //wtf.printStackTrace();
                }
            }

            return pass;
        }



        public static boolean testGetSliceEncoding() {

            boolean pass;

            try {
                pass = getSliceEncoding("Row").equals("-1,-1,1,");
                pass &= getSliceEncoding("NS").equals("-1,1,-1,");
                pass &= getSliceEncoding("EW").equals("1,-1,-1,");
            }
            catch( WTFException wtf){
                wtf.printStackTrace();
                pass = false;
            }

            String abcs = "abcdefghijklmnopqrstuvwxyzACDEFHIJKLMNPQSTUVXZ0123456789!@#$$%^&&*(),.;'[]\\/<>?:\"{}|+_-=";

            for (int i = 0; i < abcs.length(); i++) {
                try {
                    //This should throw an exception so it never gets to pass = false;
                    getSliceEncoding(abcs.charAt(i)+"");
                    pass = false;
                }
                catch( WTFException wtf){
                    //wtf.printStackTrace();
                }
            }

            return pass;
        }


    }

}
