package org.gitlab_gui.view;

import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.Callback;
import org.gitlab_gui.model.project_info.ProjectInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VisibilityManager {
    private JFrame visibilityFrame;
    private ProjectInfo projectInfo;

    private Controller controller;

    Callback callback;

    VisibilityManager(ProjectInfo projectInfo, Callback callback) {
        this.projectInfo = projectInfo;
        this.controller = Controller.getInstance();
        this.callback = callback;

        visibilityFrame = new JFrame("Visibility");
        visibilityFrame.setSize(600, 150);
        visibilityFrame.setLocationRelativeTo(null);
        //visibilityFrame.setResizable(false);
        visibilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // reset result text on exit
        visibilityFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                callback.execute();
            }
        });

        createUIComponents();

        visibilityFrame.setVisible(true);
    }

    private void createUIComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel visibilityPanel = new JPanel(new GridLayout(3, 2, 2, 1));
        visibilityPanel.add(new JLabel("Current Visibility:  "));

        JLabel visibilityLabel = new JLabel(this.projectInfo.getVisibility());
        visibilityPanel.add(visibilityLabel);

        visibilityPanel.add(new JLabel("Visibility:  "));
        String[] visibilityStrings = {"private", "internal", "public"};
        JComboBox<String> visibilityList = new JComboBox<>(visibilityStrings);
        visibilityList.setSelectedIndex(0);
        visibilityPanel.add(visibilityList);



        JButton saveButton = new JButton("Save");

        saveButton.setEnabled(false);
        saveButton.setHorizontalAlignment(JLabel.CENTER);
        saveButton.addActionListener(e -> {
            String visibility = (String) visibilityList.getSelectedItem();

            if (!controller.setProjectVisibility(visibility)) {
                JOptionPane.showMessageDialog(visibilityFrame,
                        "Error: Could not change visibility");
            } else {
                JOptionPane.showMessageDialog(visibilityFrame,
                        "Visibility changed successfully");
                visibilityLabel.setText(visibility);
            }

            callback.execute();

        });

        saveButton.setPreferredSize(new Dimension(100, 40));

        visibilityList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) e.getItem();
                saveButton.setEnabled(!selected.equals(this.projectInfo.getVisibility()));
            }
        });

        mainPanel.add(visibilityPanel, BorderLayout.NORTH);
        mainPanel.add(saveButton, BorderLayout.SOUTH);


        visibilityFrame.add(mainPanel);
    }


}
