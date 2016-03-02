package xor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by chris_000 on 2/25/2016.
 */
public class XORResultsWriter {

    public static final String DOUBLE_COMMA = ",,";

    public static void writeExperimentResults(String fileName, String extraInfo,String attr1, String attr2, String[] attr1Values, String[] attr2Values, Map<String,Map<String,Double>> results){
        try {

            File resultsFile = new File(fileName);

            String[] filenameSplit = fileName.split("\\.");
            int i=0;
            while(resultsFile.exists()) {
                System.out.print(resultsFile.getName() + " exists");
                resultsFile = new File(filenameSplit[0] + ++i + "." + filenameSplit[1]);
                System.out.println(", trying " + resultsFile.getName());
            }
            System.out.println("Creating file: " + resultsFile.getName());

            if(!resultsFile.exists())
                resultsFile.createNewFile();

            try(PrintWriter pw = new PrintWriter(new FileWriter(resultsFile))){

                //first line
                pw.write(','+extraInfo+','+attr1+'\n');

                //second line
                pw.write(DOUBLE_COMMA);
                for(String d:attr1Values)
                    pw.write(d+",");
                pw.write('\n');

                boolean first = true;
                //third line, data starts here
                for(String attr2Value:attr2Values) {
                    if (first) {
                        first = false;
                        pw.write(attr2);
                    }
                    pw.write(","+attr2Value+",");


                    for (String attr1Value : attr1Values) {
                        pw.write(results.get(attr1Value).get(attr2Value)+",");
                    }

                    pw.write('\n');
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
