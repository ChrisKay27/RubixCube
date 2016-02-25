package tests;

import cube.RubixCube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Note: Not part of the assignment spec! (So unit testing has been omitted)
 *
 * This class looks at all the results produced.
 * It recreates the initial cube, performs all the moves and checks to see if the cube is solved, if it reports an error
 * for that file.
 * Created by Chris Kaebe on 1/23/2016.
 */
public class VerifyResults {

    public static void main(String[] args) {
        File file = new File("C:\\Users\\chris_000\\SkyDrive\\Documents\\School\\CPSC 371 AI\\RubixCube\\results");
        File[] resultFiles = file.listFiles();


        results:
        for(File resultFile : resultFiles ){
            if(!resultFile.getName().endsWith(".txt"))
                continue;
            try(BufferedReader br = new BufferedReader(new FileReader(resultFile))){

                List<String> moves = new ArrayList<>();
                List<RubixCube> cubes = new ArrayList<>();

                br.lines().forEach(line -> {
                    int lastComma = line.lastIndexOf(',');

                    String cube = line.substring(1,lastComma);

                    cubes.add(new RubixCube(cube));

                    moves.add(line.substring(lastComma+1,line.length()-1));
                });


                RubixCube shouldBeEqualTo = cubes.get(0);
                for (int i = 0; i < cubes.size()-1; i++) {

                    RubixCube cube =  cubes.get(i);

                    if( !cube.equals(shouldBeEqualTo)){
                        System.err.println("File: " + resultFile.getName() + " failed on line " + i);
                        continue results;
                    }

                    String move = moves.get(i);

                    String[] parts = move.split(":");
                    int colOrRow = Integer.parseInt(parts[1]);

                    boolean cw = parts[2].equals("cw");

                    switch (parts[0]){
                        case "EW":
                            //System.out.println("Rotating ew col " + colOrRow + (cw?"cw":"ccw"));
                            shouldBeEqualTo.rotateEW(colOrRow,cw);
                            break;
                        case "NS":
                            //System.out.println("Rotating ns col " + colOrRow + (cw?"down":"up"));
                            shouldBeEqualTo.rotateNS(colOrRow,cw);
                            break;
                        case "Row":
                           // System.out.println("Rotating row " + colOrRow + (cw?"cw":"ccw"));
                            shouldBeEqualTo.rotateRow(colOrRow,cw);
                            break;
                    }
                }
                if( !shouldBeEqualTo.equals(new RubixCube(shouldBeEqualTo.getSize())) ){
                    System.out.println("File: " + resultFile.getName() + " failed");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Success! looked at " + resultFiles.length + " results!");

    }

}
