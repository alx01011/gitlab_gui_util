package org.gitlab_gui;

import org.gitlab_gui.view.MainView;

import javax.swing.*;

import static org.gitlab_gui.Utils.isGitInstalled;
import static org.gitlab_gui.Utils.saveConfigOnDisk;

public class Main {
    public static void main(String[] args) {
        // a bit prettier on windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // if the config file doesn't exist, create it with the default values
        saveConfigOnDisk();

        // check if git is installed so we can enable clone
        isGitInstalled();

        new MainView(400, 500);
    }
}