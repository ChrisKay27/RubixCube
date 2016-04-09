package ui;

import tests.TestMain;
import tests.genetics.TestGenetics;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chris on 4/9/2016.
 */
public class UnitTestGeneticsWindow extends JFrame {
    public UnitTestGeneticsWindow() {
        setSize(500,500);
        JPanel contentPane = new JPanel();
        JTextPane output = new JTextPane();
        contentPane.add(output);

        new Thread(()-> TestGenetics.main(s-> SwingUtilities.invokeLater(()->output.setText(output.getText()+'\n'+s)))    ).start();


        setContentPane(contentPane);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
