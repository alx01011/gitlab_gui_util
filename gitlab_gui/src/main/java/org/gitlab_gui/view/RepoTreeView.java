package org.gitlab_gui.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.gitlab_gui.Utils;
import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.RepoTree;
import org.gitlab_gui.model.branch.Branch;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepoTreeView {
    private final JFrame frame;
    private JPanel mainPanel;
    private JLabel branchLabel;
    private JTree fileTree;
    private JScrollPane treeScrollPane;
    private JButton addButton;
    private JButton removeButton;
    private JComboBox<String> branchCombo;
    private JButton refreshButton;
    private JButton downloadButton;
    private final ArrayList<Branch> branches;
    private final Controller controller;
    private ArrayList<RepoTree> repoTree;
    private final String repoName;
    private Branch defBranch;
    private ArrayList<DefaultMutableTreeNode> selectedNodes;

    public RepoTreeView(Branch defBranch, ArrayList<Branch> branches, ArrayList<RepoTree> repoTree, String repoName) {
        this.defBranch = defBranch;
        this.branches = branches;
        this.repoTree = repoTree;
        this.controller = Controller.getInstance();
        this.repoName = repoName;

        frame = new JFrame("RepoTreeView");
        $$$setupUI$$$();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.add(mainPanel);

        frame.setVisible(true);
    }

    private void buildTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(repoName);
        DefaultTreeModel model = new DefaultTreeModel(root);
        fileTree.setModel(model);

        // add the directories
        for (RepoTree repoEntity : repoTree) {
            if (repoEntity.getType().equals("tree")) {

                String[] path = repoEntity.getPath().split("/");
                DefaultMutableTreeNode srcDir = root;

                for (String s : path) {
                    for (int j = 0; j < srcDir.getChildCount(); j++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) srcDir.getChildAt(j);
                        if (child.getUserObject().equals(s)) {
                            srcDir = child;
                            break;
                        }
                    }
                }
                srcDir.add(new DefaultMutableTreeNode(path[path.length - 1]));
            }
        }

        // add each file to the correct dir in the tree
        for (RepoTree repoEntity : repoTree) {
            if (!repoEntity.getType().equals("tree")) {
                String[] split = repoEntity.getPath().split("/");
                String fname = split[split.length - 1]; // file name is the last

                DefaultMutableTreeNode node = root;

                for (int i = 0; i < split.length - 1; i++) {
                    // look for the dir
                    for (int j = 0; j < node.getChildCount(); j++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
                        if (child.getUserObject().equals(split[i])) {
                            node = child;
                            break;
                        }
                    }
                }

                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fname);
                newNode.setUserObject(fname);
                node.add(newNode);
            }
        }

        // show the tree
        fileTree.expandRow(0);
    }

    private String getFilePathFromNode(DefaultMutableTreeNode node) {
        if (node == null) return "";

        StringBuilder path = new StringBuilder(node.toString());
        // node = (DefaultMutableTreeNode) node.getParent(); // remove the project name

        while (node.getParent() != null) {
            path.insert(0, node.getParent().toString() + "/");
            node = (DefaultMutableTreeNode) node.getParent();
        }

        // get rid of the project name, since it's not part of the path
        path.delete(0, repoName.length() + 1);

        return path.toString();
    }

    private void addSubDirsToTree(DefaultMutableTreeNode head, ArrayList<DefaultMutableTreeNode> nodes) {

        for (int i = 0; i < head.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) head.getChildAt(i);
            if (node.getDepth() == 0) { // depth is zero, so it's a file
                nodes.add(node);
            } else {
                addSubDirsToTree(node, nodes);
            }
        }
    }

    private void createUIComponents() {
        branchCombo = new JComboBox<>();
        branchCombo.setModel(new DefaultComboBoxModel<>(branches.stream().map(Branch::getName).toArray(String[]::new)));
        branchCombo.setSelectedItem(defBranch);

        fileTree = new JTree();

        branchLabel = new JLabel("Branch:");

        buildTree();

        branchCombo.addActionListener(e -> {
                    this.repoTree = (ArrayList<RepoTree>) controller.getRepoTree(branchCombo.getSelectedItem().toString());
                    buildTree();
                    removeButton.setEnabled(false);
                }
        );

        fileTree.setToolTipText("Double click to view latest commit info or view file");
        fileTree.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 1) {
                            if (fileTree.getSelectionCount() == 0) {
                                removeButton.setEnabled(false);
                                return;
                            }
                            // add button should only be enabled if we have a dir selected
                            addButton.setEnabled(fileTree.getSelectionCount() == 1 &&
                                    ((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()).getDepth() != 0);

                            // get all selected nodes
                            if (selectedNodes == null) selectedNodes = new ArrayList<>();
                            else selectedNodes.clear();

                            TreePath[] paths = fileTree.getSelectionPaths();


                            if (paths == null) return;

                            for (TreePath path : paths) {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                                if (node.getDepth() == 0) { // depth is zero, so it's a file
                                    selectedNodes.add(node);
                                }
                                // else we have to add every file in the dir
                                else {
                                    addSubDirsToTree(node, selectedNodes);
                                    // path should now be empty, so we don't need to add the dir
                                    // it will be automatically be removed or added when we add/remove a file
                                }
                            }
                            removeButton.setEnabled(!selectedNodes.isEmpty());
                        }

                        if (e.getClickCount() == 2) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                    fileTree.getLastSelectedPathComponent();
                            if (node == null) return;
                            if (node.getDepth() == 0) { // depth is zero, so it's a file
                                // can't be an empty folder because git adds a .gitkeep file
                                // get the node path
                                String path = getFilePathFromNode(node);

                                new FileInfo(controller.getPathCommit(path, branchCombo.getSelectedItem().toString()),
                                        controller.getRepoFile(path, branchCombo.getSelectedItem().toString()), branchCombo.getSelectedItem().toString());
                            }
                        }
                    }
                });

        addButton = new JButton("Add file");
        addButton.setEnabled(true);
        addButton.addActionListener(e -> {
            // open a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);

            int result = fileChooser.showOpenDialog(null);

            if (result != JFileChooser.APPROVE_OPTION) return;

            // get the selected files
            File[] selectedFiles = fileChooser.getSelectedFiles();
            String baseDir = fileChooser.getSelectedFile().isDirectory() ? fileChooser.getSelectedFile().getName() : "";


            ArrayList<File> files = new ArrayList<>();

            Utils.addAllDirFiles(selectedFiles, files);

            List<Map<String, Object>> actions = new ArrayList<>();
            StringBuilder commitMsg = new StringBuilder("Add file: ");

            for (File file : files) {
                String filePath;
                if (baseDir.isEmpty()) {
                    filePath = file.getName();
                } else {
                    filePath = baseDir + "/" + file.getPath().substring(file.getPath().indexOf(baseDir) + baseDir.length() + 1);
                }

                String gitPath = getFilePathFromNode((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()) + "/" + filePath;
                String content = Utils.getBase64FileContent(file);

                if (content.isEmpty()) {
                    System.err.println("File: " + file.getName() + " appears to be empty, skipping");
                    continue;
                }
                // check if git path is already a node
                // if it is we have to update it
                boolean exists = false;

                for (DefaultMutableTreeNode node : selectedNodes) {
                    String treeFile = getFilePathFromNode((DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent()) + "/" + node.toString();

                    if (treeFile.equals(gitPath)) {
                        exists = true;
                        break;
                    }
                }

                Map<String, Object> action = exists ? Utils.createCommitUpdateAction(gitPath, content) : Utils.createCommitCreateAction(gitPath, content);
                commitMsg.append(file.getName()).append(", ");

                actions.add(action);
            }

            Map<String, Object> payload = Utils.createCommitPayload(commitMsg.toString(), branchCombo.getSelectedItem().toString(), actions);

            if (controller.addCommit(payload)) {
                JOptionPane.showMessageDialog(null, "File(s) added/updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error adding file(s)!\nIf the file already exists, " +
                        "try removing it or updating its content first.");
            }

            this.repoTree = (ArrayList<RepoTree>) controller.getRepoTree(branchCombo.getSelectedItem().toString());
            buildTree();
            selectedNodes.clear();
        });


        removeButton = new JButton("Remove file");
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            List<Map<String, Object>> actions = new ArrayList<>();
            StringBuilder commitMsg = new StringBuilder("Removed file: ");

            for (DefaultMutableTreeNode node : selectedNodes) {
                String path = getFilePathFromNode(node);

                Map<String, Object> action = Utils.createDeleteAction(path);
                commitMsg.append(node).append(", ");

                actions.add(action);
            }

            Map<String, Object> payload = Utils.createCommitPayload(commitMsg.toString(), branchCombo.getSelectedItem().toString(), actions);

            if (controller.addCommit(payload)) {
                JOptionPane.showMessageDialog(null, "File removed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error removing file!");
            }

            this.repoTree = (ArrayList<RepoTree>) controller.getRepoTree(branchCombo.getSelectedItem().toString());
            buildTree();
            selectedNodes.clear();
        });

        refreshButton = new JButton("Refresh");
        // set the icon for the refresh button
        ImageIcon refreshIcon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icons/Refresh_icon.png"));
        // resize
        Image image = refreshIcon.getImage();
        Image newimg = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        refreshIcon = new ImageIcon(newimg);
        refreshButton.setIcon(refreshIcon);

        refreshButton.setToolTipText("Refresh the branch list and the file tree");

        refreshButton.addActionListener(e -> {
            // get all the branches
            ArrayList<Branch> branches = (ArrayList<Branch>) controller.getRepoBranches();
            branchCombo.setModel(new DefaultComboBoxModel<>(branches.stream().map(Branch::getName).toArray(String[]::new)));
            defBranch = branches.get(0);
            this.repoTree = (ArrayList<RepoTree>) controller.getRepoTree(defBranch.getName());
            buildTree();
        });

        downloadButton = new JButton("Download Branch");

        downloadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);

            int result = fileChooser.showOpenDialog(null);

            if (result != JFileChooser.APPROVE_OPTION) return;

            String path = fileChooser.getSelectedFile().getPath();

            if (controller.downloadRepoBranch(branchCombo.getSelectedItem().toString(), path)) {
                JOptionPane.showMessageDialog(null, "Branch downloaded successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error downloading branch!");
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
        mainPanel.setLayout(new GridLayoutManager(3, 12, new Insets(0, 0, 0, 0), -1, -1));
        treeScrollPane = new JScrollPane();
        mainPanel.add(treeScrollPane, new GridConstraints(1, 0, 1, 12, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        treeScrollPane.setViewportView(fileTree);
        mainPanel.add(branchLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainPanel.add(branchCombo, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(refreshButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(downloadButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(addButton, new GridConstraints(2, 11, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel.add(removeButton, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
