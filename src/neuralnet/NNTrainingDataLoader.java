package neuralnet;

import util.WTFException;

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
        List<TrainingTuple> trainingTuples = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.lines().forEach(tupleStr->{
                try {
                    String[] tupleSplit = tupleStr.split("\\|");

                    List<Double> inputs = toDoubleList(tupleSplit[0]);

                    List<Double> outputs = toDoubleList(tupleSplit[1]);

//                System.out.println("input size " + inputs.size() + " outputs size: " + outputs.size());
                    trainingTuples.add(new TrainingTuple(inputs, outputs));
                }
                catch(Exception e){
                    System.err.println("Bad training tuple format: "+ tupleStr);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        if( trainingTuples.size() == 0 )
            throw new WTFException("Didnt load any training tuples!");

        return trainingTuples;
    }

    public static List<Double> toDoubleList(String s){
        String[] inputsSplit = s.split(",");
        List<Double> doubles = new ArrayList<>();
        for(String inputStr : inputsSplit )
            doubles.add(Double.parseDouble(inputStr));
        return doubles;
    }


}
