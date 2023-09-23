package org.gitlab_gui.view;

import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.branch.Branch;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BranchManager {
    private JFrame frame;
    private JPanel mainPanel;
    private ArrayList<Branch> branches;
    private Controller controller;


    public BranchManager(ArrayList<Branch>  branches) {
        this.branches = branches;
        this.controller = Controller.getInstance();

        frame = new JFrame("Branch Manager");
        mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        frame.setContentPane(scrollPane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        int width = branches.stream().mapToInt(branch -> branch.getName().length()).max().orElse(0) * 8 * 2;

        // in case of short branch names, set a minimum width
        if (width < 600) {
            width = 600;
        }

        frame.setPreferredSize(new java.awt.Dimension(width, 230));
        frame.setLocationRelativeTo(null);

        createUIComponents();

        frame.pack();
        frame.setVisible(true);
    }


    private void createUIComponents() {
        mainPanel.setLayout(new GridLayout(2, 1));

        JPanel branchesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        branchesPanel.setBorder(BorderFactory.createTitledBorder("Branches"));

        JComboBox<String> branchesComboBox = new JComboBox<>(Arrays.stream(branches.
                toArray(Branch[]::new)).map(Branch::getName).toArray(String[]::new));
        branchesComboBox.setEnabled(branchesComboBox.getItemCount() > 0);
        branchesPanel.add(branchesComboBox, gbc);

        gbc.insets = new Insets(5, 0, 5, 5);

        JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(branchesComboBox.getItemCount() > 0);
        removeButton.addActionListener(e -> {
            String branch = (String) branchesComboBox.getSelectedItem();
            if (!controller.deleteBranch(branch)) {
                JOptionPane.showMessageDialog(frame, "Error removing branch", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // wait for a bit, for the deletion to complete
            showLoadingDialog("Deleting branch");

            frame.dispose();

            new BranchManager((ArrayList<Branch>) controller.getRepoBranches());
        });

        branchesPanel.add(removeButton, gbc);


        mainPanel.add(branchesPanel);

        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);



        JLabel refLabel = new JLabel("Source:");
        JComboBox<String> refDropdown = new JComboBox<>(Arrays.stream(branches.
                toArray(Branch[]::new)).map(Branch::getName).toArray(String[]::new));

        JButton createButton = new JButton("Create");
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!nameField.getText().isEmpty() && !createButton.isEnabled()) {
                    createButton.setEnabled(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (nameField.getText().isEmpty() && createButton.isEnabled()) {
                    createButton.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (nameField.getText().isEmpty() && createButton.isEnabled()) {
                    createButton.setEnabled(false);
                }
            }
        });

        createButton.setEnabled(false);
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String ref = (String) refDropdown.getSelectedItem();

            if (!controller.addBranch(name, ref)) {
                JOptionPane.showMessageDialog(frame, "Error creating branch", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // wait for a bit, for the creation to complete
            showLoadingDialog("Creating branch");

            frame.dispose();

            new BranchManager((ArrayList<Branch>) controller.getRepoBranches());
        });


        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(refLabel);
        inputPanel.add(refDropdown);
        inputPanel.add(createButton);

        JPanel createPanel = new JPanel(new FlowLayout());
        createPanel.setBorder(BorderFactory.createTitledBorder("Create branch"));

        createPanel.add(inputPanel);

        mainPanel.add(createPanel);
    }

    private void showLoadingDialog(String msg) {

        // show a loading bar
        JDialog dialog = new JDialog(frame, "Loading", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(300, 100));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel(msg));
        JProgressBar progressBar = new JProgressBar();

        progressBar.setMaximum(30);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(250, 20));
        progressBar.setBorderPainted(true);

        panel.add(progressBar);

        // start a new thread to update the progress bar
        // allow for 30 seconds so that the branch update is visible
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setValue(i);
            }
            dialog.dispose();
        }).start();

        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }


}
