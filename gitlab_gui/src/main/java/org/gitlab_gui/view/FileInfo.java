package org.gitlab_gui.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.gitlab_gui.Utils;
import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.RepoFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class FileInfo {
    private Controller controller;
    private RepoFile repoFile;
    private Map<String, Object> commitInfo;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JPanel infoPanel;
    private JTable infoTable;
    private JPanel buttonPanel;
    private JButton viewButton;
    private JButton downloadButton;
    private String branch;

    public FileInfo(Map<String, Object> commitInfo, RepoFile repoFile, String branch) {
        this.commitInfo = commitInfo;
        this.controller = Controller.getInstance();
        this.repoFile = repoFile;
        this.branch = branch;

        JFrame frame = new JFrame("Commit info");
        $$$setupUI$$$();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(830, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.add(mainPanel);

        frame.setVisible(true);
    }

    private void createUIComponents() {
        int size = commitInfo.size();
        String[][] result = new String[size + 1][2]; // Each entry will have two elements: key and value

        int i = 0;

        result[i][0] = "file_name";
        result[i++][1] = repoFile.getFile_name();
        for (Map.Entry<String, Object> entry : commitInfo.entrySet()) {
            result[i][0] = entry.getKey(); // Key
            result[i][1] = String.valueOf(entry.getValue()); // Value (converted to String)
            i++;
        }

        String[] columnNames = {"Property", "Value"};
        infoTable = new JTable(result, columnNames);

        viewButton = new JButton("View file");
        downloadButton = new JButton("Download file");

        viewButton.addActionListener(e -> {
            String tmpDir = System.getProperty("java.io.tmpdir");

            File file = Utils.createFileFromBase64(repoFile.getContent(), tmpDir + "/" + repoFile.getFile_name());

            if (file == null) {
                JOptionPane.showMessageDialog(null, "Error creating file", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String content = Utils.getRawFileContent(file);

            // we can delete now
            if (!file.delete()) {
                JOptionPane.showMessageDialog(null, "Error deleting file, maybe permissions?", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new FileViewer(content, branch, repoFile);

        });

        downloadButton.addActionListener(e -> {
            // choose where to save the file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify where to save: " + repoFile.getFile_name());
            fileChooser.setSelectedFile(new File(repoFile.getFile_name()));

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                File file = Utils.createFileFromBase64(repoFile.getContent(), fileToSave.getAbsolutePath());

                if (file == null) {
                    JOptionPane.showMessageDialog(null, "Error creating file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(null, "File saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }


        });

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(infoPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scrollPane = new JScrollPane();
        infoPanel.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setViewportView(infoTable);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(buttonPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        buttonPanel.add(viewButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonPanel.add(downloadButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}