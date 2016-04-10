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

        System.out.println("Wrote " + encodedCubeStatesAndMoves.size() + " lines to " + outputFilePath);
    }



    private HashMap<String,String> getCubeStatesToMoves(){

        File inputFolder = new File(inputFolderPath);
        if(!inputFolder.exists()) {
//            System.exit(69);
            throw new WTFException("Results folder doesn't exist in the folder of the jar! Try running experiments first.");
        };

        File[] resultFiles = inputFolder.listFiles();
        if(resultFiles == null ) {
            throw new WTFException("No results files to load! (on path: " + inputFolderPath);
        }

        HashMap<String, String> stateToMove = new HashMap<>();

        ArrayList<String> lines = new ArrayList<>();


        for (File resultFile : resultFiles) {

            if (!resultFile.getName().endsWith(".txt") || !resultFile.getName().startsWith(cubeSize+"x") || resultFile.getName().equals("trainingData.txt"))
                continue;


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
    public static String getColOrRowEncoding(String colOrRowStr, int cubeSize) {

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
    public static String getDirEncoding(String s) {
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
    public static String getSliceEncoding(String s) {
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





}
