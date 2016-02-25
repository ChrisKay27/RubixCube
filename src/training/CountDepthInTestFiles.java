package training;

import java.io.*;


/**
 * Note: Not part of the assignment spec! (Just a useful class so I omitted the unit tests instead of just removing this file before handing in)
 *
 * This class counts the number depths in the experiment results files.
 * ie. if there are 10 files that show 9 moves to solve, and 5 that take 6 moves to solve
 * then this will print out: depth 9 = 10, depth 6 = 5.
 *
 * Created by Chris Kaebe on 1/22/2016.
 */
public class CountDepthInTestFiles {
    public static void main(String[] args) throws InterruptedException {
        File file = new File("C:\\Users\\chris_000\\SkyDrive\\Documents\\School\\CPSC 371 AI\\RubixCube\\results");
        File[] resultFiles = file.listFiles();

        int[] depths = new int[20];
        for(File resultFile : resultFiles ){
            if(!resultFile.getName().endsWith(".txt") || resultFile.getName().startsWith("training"))
                continue;
            try(BufferedReader br = new BufferedReader(new FileReader(resultFile))){
                Object[] lines =  br.lines().toArray();

                int size = lines.length-1;

                depths[size]++;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        for (int i = 0; i < 20; i++) {
            System.out.println("Depth " + i + " = " + depths[i]);
        }

    }
}
