package neuralnet;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by chris_000 on 2/23/2016.
 */
public class NeuralNetIO {

    public static void saveNN(NeuralNet NN, File NNFile){
        try {
            File NNFolder = new File("NN");
            if(!NNFolder.exists())
                NNFolder.mkdirs();


            StringBuilder sb = new StringBuilder();
            sb.append("NN/NN_");

            //Append info about the network (eg 2-3-3-1)
            sb.append(NN.getInputNeurons().size());

            for(List<Neuron> hiddenLayer :NN.getHiddenLayers())
                sb.append("-").append(hiddenLayer.size());

            sb.append("-").append(NN.getOutputNeurons().size());

            //Append the current data-time
            String date = new SimpleDateFormat("MM-dd-yyyy_hh-mm").format(new Date(System.currentTimeMillis()));
            sb.append("_").append(date);

            //Append the networks error
            NumberFormat formatter = new DecimalFormat("0.###E0");
            String error = formatter.format(NN.getNetworkError());
            sb.append("_Error_").append(error);


            NNFile.createNewFile();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(NNFile))) {
                oos.writeObject(NN);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static NeuralNet loadNN(File NNFile){
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(NNFile))) {

            return (NeuralNet) oos.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
