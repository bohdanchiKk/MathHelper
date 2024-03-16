package org.example.swing;

import org.example.db.DataBase;
import org.example.service.MathHelperService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MathHelperApp extends JFrame {
    private JTextField equationField;
    private JButton solveButton;
    private JButton showDataButton;
    private JButton findAllButton;
    private JButton findUniqueButton;
    private JLabel resultLabel;
    private MathHelperService mathHelperService = new MathHelperService();
    private DataBase dataBase = new DataBase();

    public MathHelperApp() {
        setTitle("Math Helper");
        setSize(400, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        equationField = new JTextField();
        solveButton = new JButton("Save");
        showDataButton = new JButton("Show Data");
        findAllButton = new JButton("Find All Equations with Root");
        findUniqueButton = new JButton("Find Unique Equations with Root");
        resultLabel = new JLabel();

        solveButton.addActionListener(e -> solveEquation());
        showDataButton.addActionListener(e -> showDataFromDatabase());
        findAllButton.addActionListener(e -> findEquationsWithRoot());
        findUniqueButton.addActionListener(e -> findUniqueEquationsWithRoot());

        panel.add(equationField);
        panel.add(solveButton);
        panel.add(showDataButton);
        panel.add(findAllButton);
        panel.add(findUniqueButton);
        panel.add(resultLabel);

        add(panel);
    }

    private void solveEquation() {
        String equation = equationField.getText();


        String result = "";
        if (mathHelperService.divide(equation)) {
            new DataBase().save(equation);
            resultLabel.setText("Everything is good");
        } else {
            resultLabel.setText("Error!!!");
        }
    }

    private void showDataFromDatabase() {

        List<String> data = mathHelperService.getFromDb();


        JPanel equationPanel = new JPanel();
        equationPanel.setLayout(new GridLayout(data.size(), 2));

        for (String equation : data) {
            JLabel equationLabel = new JLabel(equation);
            JTextField xValueField = new JTextField();
            equationPanel.add(equationLabel);
            equationPanel.add(xValueField);

            JButton solveAndSaveButton = new JButton("Solve & Save");
            solveAndSaveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String xValue = xValueField.getText();
                    Double root = (double) 0;
                    if (mathHelperService.divide(equation)){
                        root = mathHelperService.calculateTheRoot(Double.parseDouble(xValue));
                        if (root==0){
                            JOptionPane.showMessageDialog(null, "Root can't be saved!");
                            return;
                        }
                    }
                    if (mathHelperService.divide(equation)) {

                        new DataBase().saveRootToDatabase(equation,root);
                        JOptionPane.showMessageDialog(null, "Equation solved and saved successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error solving the equation!");
                    }
                }
            });
            equationPanel.add(solveAndSaveButton);
        }


        JScrollPane scrollPane = new JScrollPane(equationPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scrollPane, "Equations", JOptionPane.PLAIN_MESSAGE);
    }
    private void findEquationsWithRoot() {
        String root = JOptionPane.showInputDialog("Enter the root:");
        List<String> equations = dataBase.getEquationsWithRoot(root);
        displayResults(equations);
    }

    private void findUniqueEquationsWithRoot() {
        List<String> equations = dataBase.getUniqueEquations();
        displayResults(equations);
    }


    private void displayResults(List<String> equations) {
        StringBuilder sb = new StringBuilder();
        for (String equation : equations) {
            sb.append(equation).append("\n");
        }
        resultLabel.setText(sb.toString());
    }
}
