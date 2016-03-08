package neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 3/7/2016.
 */
public class NNTrainingDataLoader {
    public static List<TrainingTuple> loadTrainingTuples(File f){
        ArrayList<TrainingTuple> trainingTuples = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.lines().forEach(tupleStr->{
                String[] tupleSplit = tupleStr.split("\\|");

                String[] inputsSplit = tupleSplit[0].split(",");
                List<Double> inputs = new ArrayList<>();
                for(String inputStr : inputsSplit )
                    inputs.add(Double.parseDouble(inputStr));


                List<Double> outputs = new ArrayList<>();
                String[] ouputsSplit = tupleSplit[1].split(",");
                for(String outputStr : ouputsSplit )
                    outputs.add(Double.parseDouble(outputStr));

//                System.out.println("input size " + inputs.size() + " outputs size: " + outputs.size());
                trainingTuples.add(new TrainingTuple(inputs,outputs));
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        return trainingTuples;
    }
}
