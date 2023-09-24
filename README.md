# Gitlab GUI Utility
A GUI utility that allows you to manage any project hosted on a Gitlab server.

The goal of this project is to provide a simple, lightweight, and easy to use utility for quick project management.

There is nothing fancy and interesting about the UI, the goal was to make this as light as possible.

## Requirements

The project requires at least **Java 17**.

- **Windows**: You just need to download and install https://www.oracle.com/java/technologies/downloads/#java17 or greater
- **Ubuntu and rest of apt supported distros**: `sudo apt install openjdk-17-jdk` or greater
- **macOS**: `brew install openjdk@17` or greater

## Support

Currently, this utility supports the following features:
  - Accessing basic project information such as name, description, and visibility
  - Member management, including adding from a file and removing members
    ### Adding members from file

    To add members from a file, the file must be formatted as follows:
    ```
    username1_or_member_id1
    username2_or_member_id2
    username3_or_member_id3
    ```
  - Branch management, including creating and deleting branches
  - File management, including adding, removing, and editing files in a single repository
  - Project visibility management

    

## Building

To build the jar, run `mvn clean package`

## MacOS

Due to some security settings in macOS, it is impossible to list files and directories if you have launched the .jar by double clicking.

**You will have to execute it via** `java -jar gitlab_gui-[version].jar` 
**OR** use the provided `.dmg` file which contains a native build of the app.

## Windows

You can install the native windows app by downloading the provided .msi. The installer will prompt for installation directory. App can be uninstalled via Settings/Apps or via the Control panel.
