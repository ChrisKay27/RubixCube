package ui;

import javax.swing.*;
import java.awt.event.ActionListener;

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
}
