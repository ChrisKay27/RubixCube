package ui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris_000 on 3/15/2016.
 */
public class TextBox extends JPanel {
    private final JTextField textField;
    private final JLabel label;



    public TextBox(String labelText, String textBoxText, int columns) {
        this.textField = new JTextField(textBoxText,columns);
        this.label = new JLabel(labelText);
        add(label);
        add(textField);
    }

    public void setLabelText(String t) {
        label.setText(t);
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setColumns(int columns) {
        textField.setColumns(columns);
    }

    public int getColumns() {
        return textField.getColumns();
    }

    public void addActionListener(ActionListener l) {
        textField.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        textField.removeActionListener(l);
    }

    public void setText(String t) {
        textField.setText(t);
    }

    public String getText() {
        return textField.getText();
    }

    public String getSelectedText() {
        return textField.getSelectedText();
    }

    public void setEditable(boolean b) {
        textField.setEditable(b);
    }

    public boolean isEditable() {
        return textField.isEditable();
    }

    public int getInt(){
        return Integer.parseInt(textField.getText());
    }

    public double getDouble() {
        return Double.parseDouble(getText());
    }

    public List<Double> getDoubles() {
        String[] split = getText().split(":");
        String[] minMax = split[0].split("-");

        List<Double> values = new ArrayList<>();

        if( !getText().contains(":")){
            values.add(Double.parseDouble(getText()));
            return values;
        }

        double min = Double.parseDouble(minMax[0]);
        double max = Double.parseDouble(minMax[1]);
        double inc = Double.parseDouble(split[1]);

        for (double i = min; i <= max; i+=inc)
            values.add(i);

        return values;
    }

    public List<Integer> getInts() {

        String[] split = getText().split(":");
        String[] minMax = split[0].split("-");

        List<Integer> values = new ArrayList<>();

        if( !getText().contains(":")){
            values.add(Integer.parseInt(getText()));
            return values;
        }

        int min = Integer.parseInt(minMax[0]);
        int max = Integer.parseInt(minMax[1]);
        int inc = Integer.parseInt(split[1]);

        for (int i = min; i <= max; i+=inc)
            values.add(i);

        return values;
    }

}
