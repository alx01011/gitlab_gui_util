package org.gitlab_gui.view;

import org.gitlab_gui.Utils;
import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.Callback;
import org.gitlab_gui.model.RepoTree;
import org.gitlab_gui.model.branch.Branch;
import org.gitlab_gui.model.member.Member;
import org.gitlab_gui.model.project_info.ProjectInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainView {
    private final JFrame frame;
    private final JPanel mainPanel;
    private JPasswordField mainTokenField;

    private final Controller controller;

    private ProjectInfo projectInfo;
    private static final int PROJECT_ID_FIELD = 0;
    private static final int PROJECT_NAME_FIELD = 1;
    private static final int PROJECT_DESCRIPTION_FIELD = 2;
    private static final int PROJECT_VISIBILITY_FIELD = 3;
    private static final int PROJECT_PATH_FIELD = 4;
    private static final int PROJECT_CLONE_URL = 5; // http not ssh

    public MainView(int width, int height) {
        this.controller = new Controller(Utils.getServerFromDisk(), Utils.getPortFromDisk(), "0", "");

        this.frame = new JFrame("GitLab GUI");
        this.frame.setLocationRelativeTo(null);
        this.mainPanel = new JPanel();

        this.frame.setPreferredSize(new Dimension(width, height));
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.createProjectInfoComponents();
        this.createMenuBarComponents();

        this.frame.setVisible(true);
        this.frame.pack();

    }

    private void createProjectInfoComponents() {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around the main panel

        JPanel projectIdPanel = new JPanel(new GridLayout(2, 2));
        projectIdPanel.setBorder(BorderFactory.createTitledBorder("Project Info"));

        // Project ID input field
        JTextField projectIdField = new JTextField(20);
        projectIdPanel.add(new JLabel("Project URL or ID:"));
        projectIdPanel.add(projectIdField);

        // Token input field
        mainTokenField = new JPasswordField(20);
        mainTokenField.setText(Utils.getTokenFromDisk());
        mainTokenField.setEchoChar('*');

        projectIdPanel.add(new JLabel("Token:"));
        projectIdPanel.add(mainTokenField);

        // Result panel (assuming it's a text area)
        JTextArea resultTextArea = new JTextArea(10, 30);
        resultTextArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);

        JButton fileManagerButton = new JButton("File Manager");
        fileManagerButton.setEnabled(false);
        fileManagerButton.addActionListener(e -> {
            ArrayList<Branch> branches = (ArrayList<Branch>) controller.getRepoBranches();
            new RepoTreeView(branches.get(0), branches,
                    (ArrayList<RepoTree>) controller.getRepoTree(branches.get(0).getName()),
                    projectInfo.getHttp_url_to_repo().split("/")[
                            projectInfo.getHttp_url_to_repo().split("/").length - 1
                            ].split("\\.")[0]);
        });

        JButton membersManagerButton = new JButton("Members manager");
        membersManagerButton.setEnabled(false);
        membersManagerButton.addActionListener(e -> {
            try {
                ArrayList<Member> members = (ArrayList<Member>) controller.getProjectMembers();
                members.remove(0); // remove the first element which is the creator of the project

                Callback callback = () -> {
                    try {
                        projectInfo = controller.getProjectInfo();
                        setTextArea(resultTextArea);
                    } catch (Exception ex) {
                        resultTextArea.setText("Error: " + ex.getMessage());
                    }
                };

                new MemberManager(members, callback);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton visibilityButton = new JButton("Visibility Options");
        visibilityButton.setEnabled(false);
        visibilityButton.addActionListener( e ->
                {
                    Callback callback = () -> {
                        try {
                            projectInfo = controller.getProjectInfo();
                            setTextArea(resultTextArea);
                        } catch (Exception ex) {
                            resultTextArea.setText("Error: " + ex.getMessage());
                        }
                    };

                        new VisibilityManager(projectInfo, callback);
                }
        );

        JButton branchManagerButton = new JButton("Branch Manager");
        branchManagerButton.setEnabled(false);
        branchManagerButton.addActionListener(e -> {
            ArrayList<Branch> branches = (ArrayList<Branch>) controller.getRepoBranches();

            new BranchManager(branches);
        });

        JButton cloneButton = new JButton("Clone");
        cloneButton.setEnabled(false);
        cloneButton.addActionListener(e -> {

            if (projectIdField.getText().isEmpty()) {
                resultTextArea.setText("Error: Project ID field is empty");
                return;
            }

            if (mainTokenField.getPassword().length == 0) {
                resultTextArea.setText("Error: Token field is empty");
                return;
            }

            String cloneURL = projectIdField.getText();

            // if instead of a URL the user entered the project id
            // get the url from the project info
            if (!projectIdField.getText().contains("http")) {
                cloneURL = projectInfo.getHttp_url_to_repo();
                // discard protocol since we will be using a custom git clone format which allows authentication with token
                cloneURL = cloneURL.split("https://")[1];
            }

            // get the directory to clone it to
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose a directory to clone to");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().toString();
                try {
                    if (controller.gitClone(cloneURL, path + File.separator)) {
                        JOptionPane.showMessageDialog(frame,
                                "Project cloned successfully");
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Error: Could not clone project");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error: " + ex.getMessage());
                }
            }
        });

        // GetInfo button
        JButton getInfoButton = new JButton("Get Project Info");
        getInfoButton.addActionListener(e -> {
            membersManagerButton.setEnabled(false);
            visibilityButton.setEnabled(false);
            fileManagerButton.setEnabled(false);

            if (projectIdField.getText().isEmpty()) {
                resultTextArea.setText("Error: Project ID field is empty");
                return;
            }

            if (mainTokenField.getPassword().length == 0) {
                resultTextArea.setText("Error: Token field is empty");
                return;
            }

            // if it's a URL we have to call the projects api to get the id

            if (projectIdField.getText().contains("http")) {
                String[] urlParts = projectIdField.getText().split("/");
                String projectName = urlParts[urlParts.length - 1];

                try {
                    projectIdField.setText(controller.getProjectIdFromProjectName(projectName));
                } catch (Exception ex) {
                    resultTextArea.setText("Error: " + ex.getMessage());
                    return;
                }
            }


            controller.setProjectId(projectIdField.getText());
            controller.setToken(new String(mainTokenField.getPassword()));

            try {
                projectInfo = controller.getProjectInfo();

                setTextArea(resultTextArea);
                membersManagerButton.setEnabled(true);
                visibilityButton.setEnabled(true);
                fileManagerButton.setEnabled(true);
                branchManagerButton.setEnabled(true);
                cloneButton.setEnabled(Utils.isGitInstalled());
            } catch (Exception ex) {
                resultTextArea.setText("Error: " + ex.getMessage());
            }
        });


        mainPanel.add(projectIdPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel getInfoPanel = new JPanel();
        getInfoPanel.setLayout(new GridLayout(1, 1));
        getInfoPanel.add(getInfoButton);

        JPanel buttonsPanelUpper = new JPanel();
        buttonsPanelUpper.setLayout(new GridLayout(5, 1, 5, 0));
        buttonsPanelUpper.add(membersManagerButton);
        buttonsPanelUpper.add(visibilityButton);
        buttonsPanelUpper.add(fileManagerButton);
        buttonsPanelUpper.add(branchManagerButton);
        buttonsPanelUpper.add(cloneButton);

        mainPanel.add(getInfoPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // Add more vertical spacing
        mainPanel.add(Box.createVerticalStrut(10)); // Add more vertical spacing
        mainPanel.add(resultScrollPane);
        mainPanel.add(Box.createVerticalStrut(10)); // Add more vertical spacing
        mainPanel.add(buttonsPanelUpper);


        frame.add(mainPanel);
    }

    private void setTextArea(JTextArea resultTextArea) {
        String projectInfoText = """
                Project ID: %s
                Project Name: %s
                Project Description: %s
                Project Visibility: %s
                Project Members: %s
                Project Path: %s
                Project Clone URL: %s
                """.formatted(
                projectInfo.getId(),
                projectInfo.getName(),
                projectInfo.getDescription(),
                projectInfo.getVisibility(),
                controller.getProjectMembers().stream().map(Member::getName).toList().toString(),
                projectInfo.getPath(),
                projectInfo.getHttp_url_to_repo()
        );

        resultTextArea.setText(
                projectInfoText
        );
    }


    private void createMenuBarComponents() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menu = new JMenu("Settings");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("Gitlab Preferences");
        menuItem.addActionListener(e -> handleGitlabPreferences());
        menu.add(menuItem);

        JMenuItem menuItem2 = new JMenuItem("About");
        menuItem2.addActionListener(e -> JOptionPane.showMessageDialog(null,
                """
                        Gitlab GUI v%s

                        Created by:
                        Alexandros Antonakakis
                        """.formatted(Utils.getVersionFromConfigFile()), "About", JOptionPane.INFORMATION_MESSAGE));
        menu.add(menuItem2);

        JMenuItem menuItem3 = new JMenuItem("Exit");
        menuItem3.addActionListener(e -> System.exit(0));
        menu.add(menuItem3);
    }

    private void handleGitlabPreferences() {
        JFrame settingsFrame = new JFrame("Gitlab Preferences");

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));

        settingsFrame.setPreferredSize(new Dimension(400, 250));
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setResizable(false);
        settingsFrame.pack();

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(3, 2));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Preferences"));

        JTextField serverInfoField = new JTextField(20);
        JTextField portField = new JTextField(20);

        serverInfoField.setText(Utils.getServerFromDisk());
        portField.setText(Integer.toString(Utils.getPortFromDisk()));

        settingsPanel.add(new JLabel("Gitlab Server:"));
        settingsPanel.add(serverInfoField);
        settingsPanel.add(new JLabel("Port:"));
        settingsPanel.add(portField);

        JTextField tokenField = new JTextField(20);
        tokenField.setText(Utils.getTokenFromDisk());

        settingsPanel.add(new JLabel("Token:"));
        settingsPanel.add(tokenField);

        JPanel saveButtonPanel = new JPanel(new GridLayout(2, 1));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String serverInfo = serverInfoField.getText();
            String port = portField.getText();
            String token = tokenField.getText();

            Utils.updateConfigOnDisk(serverInfo, port, token);
            controller.setGitlabUrl(serverInfo);
            try {
                controller.setPort(Integer.parseInt(port));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(settingsFrame,
                        "Error: Port must be an integer");
            }
            controller.setToken(token);

            // update the mainTokenField
            mainTokenField.setText(token);
            mainTokenField.repaint();

            settingsFrame.dispose();
        });

        saveButtonPanel.add(Box.createVerticalStrut(2));
        saveButtonPanel.add(saveButton);

        mainPanel.add(settingsPanel);
        mainPanel.add(saveButtonPanel);


        settingsFrame.add(mainPanel);
        settingsFrame.setVisible(true);
    }




}





