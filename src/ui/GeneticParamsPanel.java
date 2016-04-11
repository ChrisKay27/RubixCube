package ui;

import genetics.GAParams;
import util.WTFException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 4/9/2016.
 */
public class GeneticParamsPanel extends JPanel {

    private final TextBox generationsTB;
    private final TextBox percElitesTB;
    private final TextBox percCrossOverTB;
    private final TextBox acceptableFitnessTB;
    private final TextBox percMutationsTB;
    private final TextBox popSizeTB;
    private final JLabel errorLabel;
    private final boolean singleValue;

    public GeneticParamsPanel(boolean singleValue) {
        this.singleValue = singleValue;
        JPanel content = new JPanel();

        if(singleValue) {
            generationsTB = new TextBox("Generations", "100", 8);
            percElitesTB = new TextBox("Percent Elites", "0.2", 8);
            percMutationsTB = new TextBox("Percent Mutations", "0.5", 8);
            percCrossOverTB = new TextBox("Percent CrossOver", "0.3", 8);
            acceptableFitnessTB = new TextBox("Acceptable Fitness", "0.01", 8);

            popSizeTB = new TextBox("Population Size", "100", 8);
        }
        else{
            generationsTB = new TextBox("Generations", "20-200:20", 8);
            percElitesTB = new TextBox("Percent Elites", "0.1-0.9:0.1", 8);
            percMutationsTB = new TextBox("Percent Mutations", "0.0-0.9:0.1", 8);
            percCrossOverTB = new TextBox("Percent CrossOver", "0.0-0.9:0.1", 8);
            acceptableFitnessTB = new TextBox("Acceptable Fitness", "0.01", 8);

            popSizeTB = new TextBox("Population Size", "20-200:20", 8);
        }
        errorLabel = new JLabel();


        content.add(generationsTB);
        content.add(percElitesTB);
        content.add(percMutationsTB);
        content.add(percCrossOverTB);
        content.add(popSizeTB);
        content.add(acceptableFitnessTB);
        content.add(errorLabel);
        add(content);
    }


    public GAParams getGaParams(){

        double percElite = percElitesTB.getDouble();
        double percFreaks = percMutationsTB.getDouble();
        double percCrossOver = percCrossOverTB.getDouble();

        if( percElite == 0.0 ){
            errorLabel.setText("Percent Elites cannot be 0");
            return null;
        }

        if( percElite+percFreaks+percCrossOver != 1.0 ) {
            errorLabel.setText("Percent Elites + Cross Over + Mutations must add up to 1");
            return null;
        }

        GAParams gaParams = new GAParams();
        gaParams.setPopSize(popSizeTB.getInt());
        gaParams.setGenerations(generationsTB.getInt());
        gaParams.setPercElites(percElitesTB.getDouble());
        gaParams.setPercMutations(percMutationsTB.getDouble());
        gaParams.setPercCross(percCrossOverTB.getDouble());
        gaParams.setAcceptableFitness(acceptableFitnessTB.getDouble());
        return gaParams;
    }



    public List<GAParams> getGaParamsVariations(){
        if( singleValue ) {
            errorLabel.setText("Cannot enter in ranges here.");
            return null;
        }
        List<GAParams> gaParamsList = new ArrayList<>();


        double acceptableFitness = acceptableFitnessTB.getDouble();


        List<Double> percElites = percElitesTB.getDoubles();
        List<Double> percFreaks = percMutationsTB.getDoubles();
        List<Double> percCrossOvers = percCrossOverTB.getDoubles();

        List<Integer> popSizes = popSizeTB.getInts();
        List<Integer> generations = generationsTB.getInts();

        for(double percElite : percElites )
            for(double percFreak : percFreaks )
                for(double percCrossOver : percCrossOvers )
                    for(int popSize : popSizes){
                        for(int generation : generations){

                            if( percElite == 0.0 || percElite+percFreak+percCrossOver != 1.0 )
                                continue ;


                            GAParams gaParams = new GAParams();
                            gaParams.setPopSize(popSize);
                            gaParams.setGenerations(generation);
                            gaParams.setPercElites(percElite);
                            gaParams.setPercMutations(percFreak);
                            gaParams.setPercCross(percCrossOver);
                            gaParams.setAcceptableFitness(acceptableFitness);
                            gaParamsList.add(gaParams);
                        }
                    }

        return gaParamsList;
    }

}
