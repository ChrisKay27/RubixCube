package ui;

import genetics.GAParams;

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

    public GeneticParamsPanel() {
        JPanel content = new JPanel();

        generationsTB = new TextBox("Generations","100",8);
        percElitesTB = new TextBox("Percent Elites","0.2",8);
        percCrossOverTB = new TextBox("Percent CrossOver","0.3",8);
        percMutationsTB = new TextBox("Percent Mutations","0.5",8);
        acceptableFitnessTB = new TextBox("Acceptable Fitness","0.01",8);

        popSizeTB = new TextBox("Population Size","100",8);
        errorLabel = new JLabel();


        content.add(generationsTB);
        content.add(percElitesTB);
        content.add(percCrossOverTB);
        content.add(percMutationsTB);
        content.add(popSizeTB);
        content.add(acceptableFitnessTB);
        content.add(errorLabel);
    }


    public GAParams getGaParams(){

        double percElite = percElitesTB.getDouble();
        double percFreaks = percMutationsTB.getDouble();
        double percCrossOver = percCrossOverTB.getDouble();

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
        List<GAParams> gaParamsList = new ArrayList<>();

        double percElite = percElitesTB.getDouble();
        double percFreaks = percMutationsTB.getDouble();
        double percCrossOver = percCrossOverTB.getDouble();
        double acceptableFitness = acceptableFitnessTB.getDouble();

        if( percElite+percFreaks+percCrossOver != 1.0 ) {
            errorLabel.setText("Percent Elites + Cross Over + Mutations must add up to 1");
            return null;
        }

        List<Integer> popSizes = popSizeTB.getInts();
        List<Integer> generations = generationsTB.getInts();
        for(int popSize : popSizes){
            for(int generation : generations){
                GAParams gaParams = new GAParams();
                gaParams.setPopSize(popSize);
                gaParams.setGenerations(generation);
                gaParams.setPercElites(percElite);
                gaParams.setPercMutations(percFreaks);
                gaParams.setPercCross(percCrossOver);
                gaParams.setAcceptableFitness(acceptableFitness);
                gaParamsList.add(gaParams);
            }
        }

        return gaParamsList;
    }

}
