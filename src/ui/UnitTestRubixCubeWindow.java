package ui;

import tests.NNTestMain;
import tests.TestMain;

import javax.swing.*;


/**
 * Created by chris_000 on 3/18/2016.
 */
public class UnitTestRubixCubeWindow extends JFrame{

    public UnitTestRubixCubeWindow() {
        setSize(500,500);
        JPanel contentPane = new JPanel();
        JTextPane output = new JTextPane();
        contentPane.add(output);

        new Thread(()-> TestMain.main(s-> SwingUtilities.invokeLater(()->output.setText(output.getText()+'\n'+s)))    ).start();


        setContentPane(contentPane);

        setLocationRelativeTo(null);
        setVisible(true);

    }
}
