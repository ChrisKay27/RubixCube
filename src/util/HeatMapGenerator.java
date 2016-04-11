package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * This is only usable with a MySql Database full of the training data.
 * Created by Chris on 4/10/2016.
 */
public class HeatMapGenerator {


    public static void genHeatMaps(double[][] topParamCombinations, String fileName, String xAxis, String yAxis, String query) throws Exception {

        Connection conn;

        Class.forName("com.mysql.jdbc.Driver").newInstance();


        conn = DriverManager.getConnection("jdbc:mysql://localhost/ai?" +
                    "user=RubixCube&password=rubixcube");


        for (int i = 0; i < 5; i++) {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(String.format(query,topParamCombinations[i][0],topParamCombinations[i][1],topParamCombinations[i][2]));

            //Map from popSize to a map from generations to best fitness
            Map<Integer,Map<Integer,Double>> resultsMap = new HashMap<>();
            List<Integer> yAxisLabels = new ArrayList<>();
            List<Integer> xAxisLabels = new ArrayList<>();

            while(resultSet.next()){
                int popSize = resultSet.getInt(1);
                int generation = resultSet.getInt(2);
                if( !resultsMap.containsKey(popSize))
                    resultsMap.put(popSize,new HashMap<>());

                resultsMap.get(popSize).put(generation,resultSet.getDouble(3));

                if( !xAxisLabels.contains(popSize) )
                    xAxisLabels.add(popSize);

                if( !yAxisLabels.contains(generation) )
                    yAxisLabels.add(generation);
            }

            File csvFile = new File(topParamCombinations[i][0]+"-"+topParamCombinations[i][1]+"-"+topParamCombinations[i][2]+"-"+fileName+"_Heatmap.csv");

            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

            //Write top line
            bw.write(","+xAxis+"\n");

            Collections.sort(xAxisLabels);

            //Write second line
            bw.write(yAxis+",");
            for(int popSize : xAxisLabels){
                bw.write(popSize+",");
            }

            Collections.sort(yAxisLabels);


            //Write result lines
            for(int gen : yAxisLabels ) {
                bw.write("\n"+gen);
                for (int popSize : xAxisLabels)
                    bw.write(","+resultsMap.get(popSize).get(gen));
            }

            bw.close();
        }
    }


    public static void main(String[] args) throws Exception {
        Connection conn;

        Class.forName("com.mysql.jdbc.Driver").newInstance();


        conn = DriverManager.getConnection("jdbc:mysql://localhost/ai?" +
                "user=RubixCube&password=rubixcube");

        //Gen Vs Pop Size
        if( true ) {
            //percElites	percMutations	percCrossOver
            double[][] topParamCombinations = {{0.1, 0.8, 0.1}, {0.2, 0.2, 0.6}, {0.4, 0.3, 0.3}, {0.2, 0.4, 0.4}, {0.6, 0.2, 0.2}};


            String query = "SELECT popSize, generations, bestFitness\n" +
                    "FROM ai.garesults \n" +
                    "WHERE percElites = %f AND percMutations = %f AND percCrossOver = %f AND generations = genNumber+1\n" +
                    "ORDER BY popSize, generations;";

            String fileName = "PopVsGen",  xAxis = "Population Size",  yAxis = "Generations";


            for (int i = 0; i < 5; i++) {
                Statement statement = conn.createStatement();

                ResultSet resultSet = statement.executeQuery(String.format(query,topParamCombinations[i][0],topParamCombinations[i][1],topParamCombinations[i][2]));

                //Map from popSize to a map from generations to best fitness
                Map<Integer,Map<Integer,Double>> resultsMap = new HashMap<>();
                List<Integer> yAxisLabels = new ArrayList<>();
                List<Integer> xAxisLabels = new ArrayList<>();

                while(resultSet.next()){
                    int popSize = resultSet.getInt(1);
                    int generation = resultSet.getInt(2);
                    if( !resultsMap.containsKey(popSize))
                        resultsMap.put(popSize,new HashMap<>());

                    resultsMap.get(popSize).put(generation,resultSet.getDouble(3));

                    if( !xAxisLabels.contains(popSize) )
                        xAxisLabels.add(popSize);

                    if( !yAxisLabels.contains(generation) )
                        yAxisLabels.add(generation);
                }

                File csvFile = new File(topParamCombinations[i][0]+"-"+topParamCombinations[i][1]+"-"+topParamCombinations[i][2]+"-"+fileName+"_Heatmap.csv");

                BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

                //Write top line
                bw.write(","+xAxis+"\n");

                Collections.sort(xAxisLabels);

                //Write second line
                bw.write(yAxis+",");
                for(int popSize : xAxisLabels){
                    bw.write(popSize+",");
                }

                Collections.sort(yAxisLabels);


                //Write result lines
                for(int gen : yAxisLabels ) {
                    bw.write("\n"+gen);
                    for (int popSize : xAxisLabels)
                        bw.write(","+resultsMap.get(popSize).get(gen));
                }

                bw.close();
            }
        }


        if( true ){
            //popSize	generations	 percElite
            double[][] topParamCombinations = {{120, 60, 0.1}, {180, 80, 0.1}, {140, 80, 0.1}, {180, 80, 0.2}, {160, 60, 0.1}};


            String query = "SELECT percMutations, percCrossOver, bestFitness\n" +
                    "FROM ai.garesults \n" +
                    "WHERE popSize = %f AND generations = %f AND percElites = %f AND generations = genNumber+1\n" +
                    "ORDER BY percMutations, percCrossOver;";

            String fileName = "MutationsVsCrossOver",  xAxis = "Mutations",  yAxis = "Crossover";


            for (int i = 0; i < 5; i++) {
                Statement statement = conn.createStatement();

                ResultSet resultSet = statement.executeQuery(String.format(query,topParamCombinations[i][0],topParamCombinations[i][1],topParamCombinations[i][2]));

                //Map from popSize to a map from generations to best fitness
                Map<Double,Map<Double,Double>> resultsMap = new HashMap<>();
                List<Double> yAxisLabels = new ArrayList<>();
                List<Double> xAxisLabels = new ArrayList<>();

                while(resultSet.next()){
                    double popSize = resultSet.getDouble(1);
                    double generation = resultSet.getDouble(2);
                    if( !resultsMap.containsKey(popSize))
                        resultsMap.put(popSize,new HashMap<>());

                    resultsMap.get(popSize).put(generation,resultSet.getDouble(3));

                    if( !xAxisLabels.contains(popSize) )
                        xAxisLabels.add(popSize);

                    if( !yAxisLabels.contains(generation) )
                        yAxisLabels.add(generation);
                }

                File csvFile = new File(topParamCombinations[i][0]+"-"+topParamCombinations[i][1]+"-"+topParamCombinations[i][2]+"-"+fileName+"_Heatmap.csv");
                BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

                //Write top line
                bw.write(","+xAxis+"\n");

                Collections.sort(xAxisLabels);

                //Write second line
                bw.write(yAxis+",");
                for(double popSize : xAxisLabels){
                    bw.write(popSize+",");
                }

                Collections.sort(yAxisLabels);

                //Write result lines
                for(double gen : yAxisLabels ) {
                    bw.write("\n"+gen);
                    for (double popSize : xAxisLabels)
                        bw.write(","+resultsMap.get(popSize).get(gen));
                }

                bw.close();
            }
        }
    }



}
