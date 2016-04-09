package ui;

import genetics.GAParams;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Created by Chris on 4/9/2016.
 */
public class GeneticParamsFrame extends JFrame {

    private final Consumer<GAParams> paramsConsumer;

    public GeneticParamsFrame(Consumer<GAParams> paramsConsumer) {
        super("Genetic Parameters");
        setSize(500,500);

        this.paramsConsumer = paramsConsumer;
        JPanel content = new JPanel();

        TextBox generationsTB = new TextBox("Generations","100",8);
        TextBox percElitesTB = new TextBox("Percent Elites","0.2",8);
        TextBox percCrossOverTB = new TextBox("Percent CrossOver","0.3",8);
        TextBox percMutationsTB = new TextBox("Percent Mutations","0.5",8);
        TextBox acceptableFitness = new TextBox("Acceptable Fitness","0.01",8);

        TextBox popSizeTB = new TextBox("Population Size","100",8);
        JLabel errorLabel = new JLabel();

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> {

            double percElite = percElitesTB.getDouble();
            double percFreaks = percMutationsTB.getDouble();
            double percCrossOver = percCrossOverTB.getDouble();

            if( percElite+percFreaks+percCrossOver != 1.0 ) {
                errorLabel.setText("Percent Elites + Cross Over + Mutations must add up to 1");
                return;
            }

            this.dispose();

            GAParams gaParams = new GAParams();
            gaParams.setPopSize(popSizeTB.getInt());
            gaParams.setGenerations(generationsTB.getInt());
            gaParams.setPercElites(generationsTB.getDouble());
            gaParams.setPercMutations(percMutationsTB.getDouble());
            gaParams.setPercCross(percCrossOverTB.getDouble());
            gaParams.setAcceptableFitness(acceptableFitness.getDouble());
            paramsConsumer.accept(gaParams);
        });

        content.add(generationsTB);
        content.add(percElitesTB);
        content.add(percCrossOverTB);
        content.add(percMutationsTB);
        content.add(popSizeTB);
        content.add(acceptableFitness);
        content.add(errorLabel);
        content.add(doneButton);

        setContentPane(content);
        setVisible(true);
    }


}
