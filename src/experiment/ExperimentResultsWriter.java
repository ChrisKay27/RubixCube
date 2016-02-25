package experiment;

import searches.Searchable;
import util.WTFException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class is designed for use with Rubix Cube experiments, not a generic experiments results writer.
 * Created by Chris on 1/26/2016.
 */
public class ExperimentResultsWriter {

    public static void writeResultsToFile(String resultsFolderPath, int cubeSize, List<Tuple> results ){
        File resultsFolder = new File(resultsFolderPath);
        if(!resultsFolder.exists())
            if(!resultsFolder.mkdirs())
                throw new WTFException("Unable to create the results folder!");


        String filename = resultsFolderPath + "\\" +cubeSize+"x"+cubeSize+"x"+cubeSize+"x"+"-exp";

        File f = null;
        int count=1;
        while( f == null ){
            f = new File(filename+ count++ +".txt");
            if( !f.exists() )
                try {
                    boolean created = f.createNewFile();
                    if( !created )
                        f = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                f = null;
        }


        try(PrintWriter pw = new PrintWriter(f)) {
            for (Tuple t : results)
                pw.write(t + "\n");
            pw.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static class TestExperimentResultsWriter {

        public static boolean test() {
            //Setup
            String resultsPath = "resultsTest";
            List<Tuple> tuples = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                final int resultNum = i;
                tuples.add(new Tuple(new Searchable() {
                    @Override
                    public List<EdgeChildPair> getChildren() {
                        return null;
                    }
                    @Override
                    public int heuristicDistanceTo(Searchable s) {
                        return 0;
                    }

                    @Override
                    public String toString() {
                        return "Result" + resultNum;
                    }
                },"Edge" + resultNum));
            }

            writeResultsToFile(resultsPath,3,tuples);


            //Testing
            File resultsFolder = new File("resultsTest");
            if( !resultsFolder.exists() )
                return false;

            boolean pass = true;

            File[] results = resultsFolder.listFiles();
            try {
                if (results == null || results.length != 1)
                    pass = false;

                if (pass) {
                    try (BufferedReader br = new BufferedReader(new FileReader(results[0]))) {

                        String line = br.readLine();
                        int count = 0;
                        while (line != null) {
                            if( !line.equals("(Result" + count + ",Edge" + count + ")")){
                                pass = false;
                                break;
                            }
                            line = br.readLine();
                            count++;
                        }
                    }
                }

            }
            catch(Exception e){
                pass = false;
            }
            finally {
                //Cleanup

                if (results != null)
                    for (File f : results)
                        f.delete();

                resultsFolder.delete();

            }

            return pass;
        }

    }
}
