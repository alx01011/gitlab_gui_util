package org.gitlab_gui.view;

import org.gitlab_gui.Utils;
import org.gitlab_gui.controller.Controller;
import org.gitlab_gui.model.Callback;
import org.gitlab_gui.model.member.Member;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;

public class MemberManager {
    private final JFrame frame;
    private final JPanel mainPanel;
    private final ArrayList<Member> members;
    private final Controller controller;

    Callback callback;

    public MemberManager(ArrayList<Member> members, Callback callback) {
        this.members = members;
        this.controller = Controller.getInstance();
        this.callback = callback;

        frame = new JFrame("Members Manager");
        mainPanel = new JPanel();

        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // on exit callback
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                callback.execute();
            }
        });


        frame.setPreferredSize(new java.awt.Dimension(600, 270));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        createUIComponents();

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        mainPanel.setLayout(new GridLayout(3, 1));

        JPanel membersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        membersPanel.setBorder(BorderFactory.createTitledBorder("Members"));

        JComboBox<String> membersComboBox = new JComboBox<>(members.stream().map(member -> member.getName() + " (" + member.getUsername() + ")").toArray(String[]::new));
        membersComboBox.setEnabled(membersComboBox.getItemCount() > 0);
        membersPanel.add(membersComboBox, gbc);

        gbc.insets = new Insets(5, 0, 5, 5);

        JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(membersComboBox.getItemCount() > 0);

        removeButton.addActionListener(e -> {
            String id = String.valueOf(members.get(membersComboBox.getSelectedIndex()).getId());
            controller.deleteMembers(id);
            frame.dispose();
            try {
                ArrayList<Member> members = (ArrayList<Member>) controller.getProjectMembers();
                members.remove(0);

                new MemberManager(members, callback);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        membersPanel.add(removeButton, gbc);

        mainPanel.add(membersPanel);

        JPanel addMemberPanel = new JPanel(new GridLayout(2, 2));
        addMemberPanel.setBorder(BorderFactory.createTitledBorder("Add Member"));

        JLabel addMemberLabel = new JLabel("Add a new member:");
        addMemberPanel.add(addMemberLabel);
        addMemberLabel.setToolTipText("Enter member id or username, you can add more than one member by separating them with a comma (,)");
        JTextField newMember = new JTextField();
        newMember.setToolTipText("Enter member id or username, you can add more than one member by separating them with a comma (,)");

        JLabel addAsLabel = new JLabel("Add as:");

        JComboBox<String> addAs = new JComboBox<>();
        ArrayList<String> roles =  new ArrayList<>();
        roles.add("Guest");
        roles.add("Reporter");
        roles.add("Developer");
        roles.add("Maintainer");

        for (String role : roles) {
            addAs.addItem(role);
        }


        JPanel addMemberButtonPanel = new JPanel(new BorderLayout());
        JButton addMemberButton = new JButton("Add Members");
        addMemberButton.setEnabled(false);

        addMemberButton.addActionListener(e -> {
            String text = newMember.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a member id", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] membersCommaSep = text.split(",");
            StringBuilder membersString = new StringBuilder();

            // we might have gotten comma separated usernames, so we have to get the id for each one
            for (String member : membersCommaSep) {
                if (!member.matches("[0-9]+")) {
                    String id = controller.getUserIdFromUname(member);
                    if (id == null) {
                        JOptionPane.showMessageDialog(frame, "Error getting id for member: " + member, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    membersString.append(id).append(",");
                } else {
                    membersString.append(member).append(",");
                }
            }

            // since the roles are
            // => Guest: 10
            // => Reporter: 20
            // => Developer: 30
            // => Maintainer: 40
            // we multiply the index of the role+1 by 10 to get the correct value
            if (!controller.addMembers(membersString.toString(), (roles.indexOf(addAs.getSelectedItem().toString()) + 1) * 10)) {
                JOptionPane.showMessageDialog(frame, "Error adding members", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            frame.dispose();
            try {
                ArrayList<Member> members = (ArrayList<Member>) controller.getProjectMembers();
                members.remove(0);

                new MemberManager(members, callback);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        addMemberButton.setPreferredSize(new Dimension(300, 50));



        JButton addMembersFromFileButton = new JButton("Add Members from file");
        addMembersFromFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    ArrayList<String> members = Utils.getMembersFromFile(path);

                    // since some members might be in the username formatm, we have to get the id
                    for (int i = 0; i < members.size(); i++) {
                        String member = members.get(i);

                        // if its only numbers, we can skip
                        if (member.matches("[0-9]+")) {
                            continue;
                        }

                        // if its not only numbers, we have to get the id
                        String id = controller.getUserIdFromUname(member);
                        if (id == null) {
                            JOptionPane.showMessageDialog(frame, "Error getting id for member: " + member, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        members.set(i, id);
                    }

                    // form a string with the members separated by a comma
                    String membersString = String.join(",", members);

                    // inform about the members to be added
                    int dialogResult = JOptionPane.showConfirmDialog(frame, "The following members will be added: " + membersString, "Confirm", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION) {
                        return;
                    }

                    controller.addMembers(membersString, (roles.indexOf(addAs.getSelectedItem().toString()) + 1) * 10);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //controller.addMembersFromFile(path);
                frame.dispose();
                try {
                    ArrayList<Member> members = (ArrayList<Member>) controller.getProjectMembers();
                    members.remove(0);
                    new MemberManager(members, callback);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addMembersFromFileButton.setPreferredSize(new Dimension(300, 50));

        newMember.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                addMemberButton.setEnabled(true);
                addMembersFromFileButton.setEnabled(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (newMember.getText().isEmpty()) {
                    addMemberButton.setEnabled(false);
                    addMembersFromFileButton.setEnabled(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (newMember.getText().isEmpty()) {
                    addMemberButton.setEnabled(false);
                    addMembersFromFileButton.setEnabled(true);
                }
            }
        });




        addMemberButtonPanel.add(addMemberButton, BorderLayout.EAST);
        addMemberButtonPanel.add(addMembersFromFileButton, BorderLayout.WEST);

        addMemberPanel.add(newMember);
        addMemberPanel.add(addAsLabel);
        addMemberPanel.add(addAs);




        mainPanel.add(addMemberPanel);
        mainPanel.add(addMemberButtonPanel);
    }
}

