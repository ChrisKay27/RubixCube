package ui;

import genetics.GAParams;

import javax.swing.*;
import javax.swing.border.Border;
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

        JPanel content = new JPanel(new BorderLayout());
        GeneticParamsPanel gpp = new GeneticParamsPanel(true);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> {
            GAParams params = gpp.getGaParams();
            if(params != null )
                paramsConsumer.accept(params);
        });


        content.add(gpp, BorderLayout.CENTER);
        content.add(doneButton,BorderLayout.SOUTH);


        setContentPane(content);
        setVisible(true);
    }


}
