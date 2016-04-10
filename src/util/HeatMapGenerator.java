package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
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
            List<Integer> generations = new ArrayList<>();
            List<Integer> popSizes = new ArrayList<>();

            while(resultSet.next()){
                int popSize = resultSet.getInt(1);
                int generation = resultSet.getInt(2);
                if( !resultsMap.containsKey(popSize))
                    resultsMap.put(popSize,new HashMap<>());

                resultsMap.get(popSize).put(generation,resultSet.getDouble(3));

                if( !popSizes.contains(popSize) )
                    popSizes.add(popSize);

                if( !generations.contains(generation) )
                    generations.add(generation);
            }

            File csvFile = new File(topParamCombinations[i][0]+"-"+topParamCombinations[i][1]+"-"+topParamCombinations[i][2]+"-"+fileName+"_Heatmap.csv");

            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

            //Write top line
            bw.write(","+xAxis+"\n");

            Collections.sort(popSizes);

            //Write second line
            bw.write(yAxis+",");
            for(int popSize : popSizes){
                bw.write(popSize+",");
            }

            Collections.sort(generations);


            //Write result lines
            for(int gen : generations ) {
                bw.write("\n"+gen);
                for (int popSize : popSizes)
                    bw.write(","+resultsMap.get(popSize).get(gen));
            }

            bw.close();
        }
    }


    public static void main(String[] args) throws Exception {


        //Gen Vs Pop Size
        if( false ) {
            //percElites	percMutations	percCrossOver
            double[][] topParamCombinations = {{0.1, 0.8, 0.1}, {0.2, 0.2, 0.6}, {0.4, 0.3, 0.3}, {0.2, 0.4, 0.4}, {0.6, 0.2, 0.2}};


            String query = "SELECT popSize, generations, bestFitness\n" +
                    "FROM ai.garesults \n" +
                    "WHERE percElites = %f AND percMutations = %f AND percCrossOver = %f AND generations = genNumber+1\n" +
                    "ORDER BY popSize, generations;";
            genHeatMaps(topParamCombinations,"PopVsGen","PopSize","Generations", query);
        }

        if( true ){
            //popSize	generations	 percElite
            double[][] topParamCombinations = {{120, 60, 0.1}, {180, 80, 0.1}, {140, 80, 0.1}, {180, 80, 0.2}, {160, 60, 0.1}};


            String query = "SELECT percMutations, percCrossOver, bestFitness\n" +
                    "FROM ai.garesults \n" +
                    "WHERE popSize = %f AND generations = %f AND percElites = %f AND generations = genNumber+1\n" +
                    "ORDER BY percMutations, percCrossOver;";
            genHeatMaps(topParamCombinations,"MutationsVsCrossOver","Mutations","CrossOver", query);

        }

    }



}
