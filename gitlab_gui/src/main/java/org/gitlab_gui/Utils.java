package org.gitlab_gui;

import java.io.*;
import java.util.*;

public class Utils {
    /**
     * Saves the config file on disk.
     * On Windows it's under %APPDATA%\Gitlab_GUI\info.config
     * On linux it's under ~/.config/Gitlab_GUI/info.config
     */
    public static void saveConfigOnDisk() {
        FileWriter fw;

        // check if we are on windows or unix
        String os = System.getProperty("os.name").toLowerCase();
        String path;

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "\\Gitlab_GUI\\info.config";
        } else { // assuming unix-like OS
            path = System.getProperty("user.home") + "/.config/Gitlab_GUI/info.config";
        }

        // if the path doesn't exist, create it
        File f = new File(path);
        if (!f.exists()) {
            if (!f.getParentFile().mkdirs()) {
                // maybe the directory already exists, try to create the file
                try {
                    if (!f.createNewFile()) {
                        System.err.println("Failed to create config file.");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } else {
            return; // the file already exists
        }

        try {
            fw = new FileWriter(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            fw.write("server=" + getServerFromConfigFile() + "\n");
            fw.write("port=" + getPortFromConfigFile() + "\n");
            fw.write("token=" + getTokenFromConfigFile() + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getServerFromConfigFile() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("info.config");

        if (input == null) {
            System.out.println("Sorry, unable to find info.config");
            return "gitlab.com";
        }

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("server")) {
                    return line.split("=")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "gitlab.com";
        }

        return "gitlab.com"; // default
    }

    public static int getPortFromConfigFile()  {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("info.config");

        if (input == null) {
            System.out.println("Sorry, unable to find info.config");
            return 443;
        }

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("port")) {
                    return Integer.parseInt(line.split("=")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 443;
        }

        return 433; // default
    }

    public static File createFileFromBase64(String base64, String path) {
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64);
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedBytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static String getTokenFromConfigFile()  {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("info.config");

        if (input == null) {
            System.out.println("Sorry, unable to find info.config");
            return "";
        }

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("token")) {
                    return line.split("=")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return ""; // default
    }

    public static String getVersionFromConfigFile()  {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("info.config");

        if (input == null) {
            System.out.println("Sorry, unable to find info.config");
            return "";
        }

        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);

        try {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("version")) {
                    return line.split("=")[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return ""; // default
    }


    public static String getServerFromDisk() {
        // if the path doesn't exist return the default
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "\\Gitlab_GUI\\info.config";
        } else  {
            path = System.getProperty("user.home") + "/.config/Gitlab_GUI/info.config";
        }

        File f = new File(path);
        if (!f.exists()) {
            return getServerFromConfigFile();
        }

        FileReader fr;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            return getServerFromConfigFile();
        }

        try (InputStream input = new FileInputStream(f)) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);

            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("server")) {
                    if (line.split("=").length < 2) {
                        return getServerFromConfigFile();
                    }

                    return line.split("=")[1];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return getServerFromConfigFile();
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fall through
        return getServerFromConfigFile();
    }

    public static String getRawFileContent(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();

            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
                sb.append("\n");
            }

            fr.close();
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getBase64FileContent(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();

            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
                sb.append("\n");
            }

            fr.close();
            br.close();
            return Base64.getEncoder().encodeToString(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getPortFromDisk() {
        // if the path doesn't exist return the config file entry
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "\\Gitlab_GUI\\info.config";
        } else  {
            path = System.getProperty("user.home") + "/.config/Gitlab_GUI/info.config";
        }

        File f = new File(path);
        if (!f.exists()) {
            return getPortFromConfigFile();
        }

        FileReader fr;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            return getPortFromConfigFile();
        }

        try (InputStream input = new FileInputStream(f)) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);

            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("port")) {
                    if (line.split("=").length < 2) {
                        return getPortFromConfigFile();
                    }
                    // try to parse the port
                    try {
                        return Integer.parseInt(line.split("=")[1]);
                    } catch (NumberFormatException e) {
                        return getPortFromConfigFile();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return getPortFromConfigFile();
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fall through
        return getPortFromConfigFile();
    }

    public static String getTokenFromDisk() {
        // if the path doesn't exist return the config file entry
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "\\Gitlab_GUI\\info.config";
        } else {
            path = System.getProperty("user.home") + "/.config/Gitlab_GUI/info.config";
        }

        File f = new File(path);
        if (!f.exists()) {
            return getTokenFromConfigFile();
        }

        FileReader fr;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            return getTokenFromConfigFile();
        }

        try (InputStream input = new FileInputStream(f)) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);

            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("token")) {
                    if (line.split("=").length < 2) {
                        return getTokenFromConfigFile();
                    }
                    String base64 = line.split("=")[1];

                    byte[] decodedBytes = Base64.getDecoder().decode(base64);
                    return new String(decodedBytes);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return getTokenFromConfigFile();
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fall through
        return getTokenFromConfigFile();
    }

    public static void updateConfigOnDisk(String srv, String port, String token) {
        String os = System.getProperty("os.name").toLowerCase();
        String path = "";

        if (os.contains("win")) {
            path = System.getenv("APPDATA") + "\\Gitlab_GUI\\info.config";
        } else {
            path = System.getProperty("user.home") + "/.config/Gitlab_GUI/info.config";
        }

        File f = new File(path);
        if (!f.exists()) {
           // create the file
            saveConfigOnDisk();
        }


        FileReader fr;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to create filereader in config update.");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        try (InputStream input = new FileInputStream(f)) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);


            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains("server")) {
                    map.put("server", srv);
                }  else if (line.contains("port")) {
                    map.put("port", port);
                } else if (line.contains("token")) {
                    String base64 = Base64.getEncoder().encodeToString(token.getBytes());
                    map.put("token", base64);
                }
                else {
                    map.put(line.split("=")[0], line.split("=")[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write the new file
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                bw.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> createCommitCreateAction(String filePath, String base64Content) {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "create");
        map.put("file_path", filePath);
        map.put("encoding", "base64");
        map.put("content", base64Content);
        return map;
    }

    public static Map<String, Object> createCommitUpdateAction(String filePath, String base64Content) {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "update");
        map.put("file_path", filePath);
        map.put("encoding", "base64");
        map.put("content", base64Content);
        return map;
    }



    public static Map<String, Object> createDeleteAction(String filePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "delete");
        map.put("file_path", filePath);
        return map;
    }

    public static Map<String, Object> createUpdateAction(String filePath, String base64Content) {
        Map<String, Object> map = new HashMap<>();
        map.put("action", "update");
        map.put("file_path", filePath);
        map.put("encoding", "base64");
        map.put("content", base64Content);
        return map;
    }

    public static Map<String, Object> createCommitPayload(String commitMsg, String branch, List<Map<String, Object>> actions) {
        Map<String, Object> map = new HashMap<>();
        map.put("branch", branch);
        map.put("commit_message", commitMsg);
        map.put("actions", actions);

        return map;

    }

    public static void addAllDirFiles(File[] dirFiles, ArrayList<File> files) {
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                if (file.listFiles() == null) {
                    continue; // empty dir
                }
                addAllDirFiles(file.listFiles(), files);
            } else {
                files.add(file);
            }
        }
    }

    // returns a list of members from a .member file
    /*
        The file should have the format,
        username1_or_id1
        username2_or_id2
        ...
        usernameN_or_idN
     */
    public static ArrayList<String> getMembersFromFile(String filePath) throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new Exception(".member file doesn't exist");
        }

        ArrayList<String> members = new ArrayList<>();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            for (String line; (line = br.readLine()) != null; ) {
                members.add(line);
            }

            fr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error reading file");
        }

        return members;
    }

    // We will only call this once and cache the result
    // call to Runtime is expensive
    private static Boolean git;
    public static boolean isGitInstalled() {
        if (git != null) {
            return git;
        }

        // check if system is windows or unix
        String os = System.getProperty("os.name").toLowerCase();
        String[] cmd;


        if (os.contains("win")) {
            cmd = new String[]{"where", "git"};
        } else {
            cmd = new String[]{"which", "git"};
        }

        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return git = p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return git = false;
        }

    }
}
