package org.gitlab_gui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.gitlab_gui.model.RepoFile;
import org.gitlab_gui.model.RepoTree;
import org.gitlab_gui.model.branch.Branch;
import org.gitlab_gui.model.member.Member;
import org.gitlab_gui.model.project_info.ProjectInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

// TODO: this should be split into multiple classes, one for each different api path call, e.g members,projects etc
// TODO: instead of the functions returning true or false, make use of custom exceptions
public class Controller {
    private String gitlabUrl;
    private String projectId;
    @Getter
    private String token;
    private int port;
    // TODO: Should add a log on every api call
    private final Logger logger;
    private static Controller instance;


    // singleton pattern except the constructor is public
    public static Controller getInstance() {
        // if its initialized we just exit
        if (instance == null) {
            System.err.println("Controller instance is null. You should call getInstance(String gitlabUrl, int port, String projectId, String token) first.");
            System.exit(1);
        }

        return instance;
    }

    public Controller(String gitlabUrl, int port, String projectId, String token) {
        this.gitlabUrl = gitlabUrl;
        this.projectId = projectId;
        this.token = token;
        this.port = port;
        this.logger = Logger.getLogger("org.gitlab_gui.controller.Controller");

        instance = this;
    }

    public void setGitlabUrl(String gitlabUrl) {
        this.gitlabUrl = gitlabUrl;
    }

    public void setPort(int port) {
        this.port = port;
        // this.gitlabUrl = this.gitlabUrl.split(":")[0] + ":" + port;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String getJsonMember(StringBuilder content, String member) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            TypeReference<HashMap<String, Object>> typeRef
                    = new TypeReference<>() {
            };

            HashMap<String, Object> o = mapper.readValue(content.toString(), typeRef);
            if (!o.containsKey(member) || o.get(member) == null) {
                return "null";
            }
            return o.get(member).toString();
        } catch (Exception e) {
            return """
                    There was an error while trying to fetch the project's info.
                    Mention the error below to the developer.
                                    
                    Error: %s
                             
                    """ + e.getMessage();
        }
    }

    public List<Member> getProjectMembers() {
        try {
            // curl --header "PRIVATE-TOKEN: <your_access_token>"
            // "https://gitlab.example.com/api/v4/projects/:id/members"

            String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/" + this.projectId + "/members";
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();


            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);
            // port

            con.connect();

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            // we will use the member class to deserialize
            ObjectMapper mapper = new ObjectMapper();

            TypeReference<List<Member>> typeRef
                    = new TypeReference<>() {
            };

            return mapper.readValue(content.toString(), typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProjectInfo getProjectInfo() throws Exception {
        // curl --request GET "https://gitlab.example.com/api/v4/projects"
        // --header "PRIVATE-TOKEN: <your_access_token>"

        ArrayList<String> projectInfo = new ArrayList<>();

        String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/" + this.projectId;
        URI uri = URI.create(uriString);


        HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("PRIVATE-TOKEN", this.token);
        con.connect();

        // get response body
        BufferedReader in = new BufferedReader(
                new java.io.InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();


        // mapper should ignore unknown properties

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeReference<ProjectInfo> typeRef
                = new TypeReference<>() {
        };


        return mapper.readValue(content.toString(), typeRef);

//        projectInfo.add("Project ID: " + this.projectId);
//        projectInfo.add("Project name: " + this.getJsonMember(content, "name"));
//        projectInfo.add("Project description: " + this.getJsonMember(content, "description"));
//        projectInfo.add("Project visibility: " + this.getJsonMember(content, "visibility"));
//        projectInfo.add("Project path: " + this.getJsonMember(content, "path_with_namespace"));
//        projectInfo.add("Project clone url: " + this.getJsonMember(content, "http_url_to_repo"));
//        projectInfo.add("Project members: " + this.getProjectMembers().stream().map(Member::getName)
//                .reduce("", (a, b) -> a + ", " + b).substring(2));
//
//
//
//        return projectInfo;
    }

    public boolean setProjectVisibility(String visibility) {
        // curl --request PUT "https://gitlab.example.com/api/v4/projects/:id"
        // --header "PRIVATE-TOKEN: <your_access_token>"
        // --data "visibility=internal"

        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/" + this.projectId;
            URI uri = URI.create(uriString);
            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("PUT");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"visibility\": \"" + visibility + "\"}";

            try (java.io.OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            System.out.println(content);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getUserIdFromUname(String uname) {
        // https://gitlab.csd.uoc.gr/api/v4/users?username=uname
        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/users?username=" + uname;
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            ObjectMapper mapper = new ObjectMapper();

            TypeReference<List<Map<String, Object>>> typeRef
                    = new TypeReference<>() {
            };

            List<Map<String, Object>> objects = mapper.readValue(content.toString(), typeRef);

            return objects.get(0).get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addMembers(String members_comma, int access_level) {
        // curl --request POST "https://gitlab.example.com/api/v4/projects/:id/members"
        // --header "PRIVATE-TOKEN: <your_access_token>"
        // --data "user_id=1&access_level=30"
        try {
            // parse comma separated values
            String[] members = members_comma.split(",");
            for (String s : members) {
                // https://gitlab.csd.uoc.gr:443/api/v4/projects/1763/members/

                String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/" + this.projectId + "/members" + "?user_id=" + s.trim() + "&access_level=" + access_level;
                URI uri = URI.create(uriString);

                uri.toURL().getDefaultPort();

                HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

                con.setRequestProperty("Host", this.gitlabUrl + ":" + this.port);
                con.setRequestMethod("POST");
                con.setRequestProperty("PRIVATE-TOKEN", this.token);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                String jsonInputString = "{\"user_id\": \"" + s.trim() + "\", \"access_level\": \"" + access_level + "\"}";

                try (java.io.OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    con.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (con.getResponseCode() == 409) {
                    System.err.println("User " + s.trim() + " already exists in project " + this.projectId);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean deleteMembers(String members_comma) {
        // curl --request POST "https://gitlab.example.com/api/v4/projects/:id/members"
        // --header "PRIVATE-TOKEN: <your_access_token>"
        // --data "user_id=1&access_level=30"
        try {
            // parse comma separated values
            String[] members = members_comma.split(",");
            for (String s : members) {
                String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/" + this.projectId + "/members/" + s.trim();
                URI uri = URI.create(uriString);

                HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

                con.setRequestMethod("DELETE");
                con.setRequestProperty("PRIVATE-TOKEN", this.token);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                con.connect();

                if (con.getResponseCode() == 404) {
                    System.err.println("User " + s.trim() + " doesn't exist in project " + this.projectId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<RepoTree> getRepoTree(String branch) {
        // curl --header "PRIVATE-TOKEN: <your_access_token>"
        // recursive = true
        // "https://gitlab.example.com/api/v4/projects/:id/repository/tree?recursive=true"


        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/"
                    + this.projectId + "/repository/tree?recursive=true&per_page=100&ref=" + branch;
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            ObjectMapper mapper = new ObjectMapper();

            TypeReference<List<RepoTree>> typeRef
                    = new TypeReference<>() {
            };

            // remove tree type from the list for now, we will add it back later when dir view is ready
            //repoTree.removeIf(repo -> repo.getType().equals("tree"));
            return mapper.readValue(content.toString(), typeRef);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean downloadRepoBranch(String branch, String path) {
        //curl --header "PRIVATE-TOKEN: token"
        // "https://gitlab.csd.uoc.gr/api/v4/projects/1763/repository/archive.zip?sha=test"
        // --output file

        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port + "/api/v4/projects/"
                    + this.projectId + "/repository/archive.zip?sha=" + branch;
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            // get response body
            java.io.InputStream in = con.getInputStream();

            // create a new file
            File file = new File(path + File.separator + branch + ".zip");

            // create a new file output stream
            java.io.FileOutputStream out = new java.io.FileOutputStream(file);

            // create a buffer to read data in chunks
            byte[] buffer = new byte[1024];
            int length;

            // read data from the input stream and store it in the buffer
            while ((length = in.read(buffer)) != -1) {
                // write data from the buffer to the output stream
                out.write(buffer, 0, length);
            }

            // close the output stream
            out.close();
            in.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RepoFile getRepoFile(String path, String branch) {
        try {
            // we will URL encode the path
            // this custom method of constructing the uri seems easier
            String uriString = "https://" + this.gitlabUrl + ":" + port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/files/" + URLEncoder.encode(path, StandardCharsets.UTF_8) + "?ref=" + branch;

            URI uri = URI.create(uriString);


            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            ObjectMapper mapper = new ObjectMapper();

            TypeReference<RepoFile> typeRef
                    = new TypeReference<>() {
            };

            return mapper.readValue(content.toString(), typeRef);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getPathCommit(String path, String branch) {
        try {
            String uriString = "https://" + this.gitlabUrl + ":" + port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/commits?ref_name=" + branch + "&path="
                    + URLEncoder.encode(path, StandardCharsets.UTF_8);
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            ObjectMapper mapper = new ObjectMapper();

            TypeReference<List<Map<String, Object>>> typeRef
                    = new TypeReference<>() {
            };

            List<Map<String, Object>> objects = mapper.readValue(content.toString(), typeRef);

            return objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProjectInfo getProjectFromURI(String puri) {
        //https://gitlab.com/api/v4/projects?owned=true&search=pname

        String pname = puri.split("/")[puri.split("/").length - 1].split("\\.")[0];

        try {
            String uriString = "https://" + this.gitlabUrl + ":" + port
                    + "/api/v4/projects?owned=true&search=" + pname;
            URI uri = URI.create(uriString);
            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.addRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());

            // get response body
            BufferedReader in = new BufferedReader(inputStreamReader);

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeReference<List<ProjectInfo>> typeRef
                    = new TypeReference<>() {
            };

            List<ProjectInfo> objects = mapper.readValue(content.toString(), typeRef);

            for (ProjectInfo projectInfo : objects) {
                String projectName = projectInfo.getWeb_url().split("/")[projectInfo.getWeb_url().split("/").length - 1].split("\\.")[0];
                if (projectName.equals(pname)) {
                    return projectInfo;
                }
            }
            return null;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean addCommit(Map<String, Object> payload) {https://gitlab.csd.uoc.gr/csd48
        // curl --request POST --header "PRIVATE-TOKEN: <your_access_token>" --header "Content-Type: application/json" \
        //     --data "$PAYLOAD" "https://gitlab.example.com/api/v4/projects/1/repository/commits"

        try {
            String uriString = "https://" + this.gitlabUrl + ":" + port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/commits";

            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String jsonInputString = mapper.writeValueAsString(payload);

            System.out.println(jsonInputString);

            try (java.io.OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                con.connect();

                if (con.getResponseCode() != 201) {
                    System.err.println("There was an error while trying to connect to the server. Maybe your payload is invalid. Response code: " + con.getResponseCode());

                    return false;
                }
            } catch (Exception e) {
                System.err.println("There was an error while trying to connect to the server. Maybe your payload is invalid.");
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            System.err.println("There was an error parsing the commit url.");
            return false;
        }

        return true;
    }

    public List<Branch> getRepoBranches() {
        try {
            String uriString = "https://" + this.gitlabUrl + ":" + port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/branches" + "?per_page=50";
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);

            con.connect();

            // get response body
            BufferedReader in = new BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeReference<List<Branch>> typeRef
                    = new TypeReference<>() {
            };

            return  mapper.readValue(content.toString(), typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBranch(String name, String ref) {
        // curl --request POST
        // --header "PRIVATE-TOKEN: <your_access_token>"
        // "https://gitlab.example.com/api/v4/projects/5/repository/branches?branch=newbranch&ref=main"
        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/branches?branch=" + name + "&ref=" + ref;
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            con.connect();

            if (con.getResponseCode() != 201) {
                System.err.println("There was an error while trying to connect to the server. Maybe your payload is invalid. Response code: " + con.getResponseCode());

                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBranch(String name) {
        // curl --request DELETE
        // --header "PRIVATE-TOKEN: <your_access_token>"
        // "https://gitlab.example.com/api/v4/projects/5/repository/branches/newbranch"

        try {
            String uriString = "https://" + this.gitlabUrl + ":" + this.port
                    + "/api/v4/projects/"
                    + this.projectId + "/repository/branches/" + name;
            URI uri = URI.create(uriString);

            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();

            con.setRequestMethod("DELETE");
            con.setRequestProperty("PRIVATE-TOKEN", this.token);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            con.connect();

            if (con.getResponseCode() != 204) {
                System.err.println("There was an error while trying to connect to the server. Maybe your payload is invalid. Response code: " + con.getResponseCode());

                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean gitClone(String url, String destDir) {
        // git clone https://oauth2:ACCESS_TOKEN@somegitlab.com/vendor/package.git destdir
        String cloneURL = "https://" + "oauth2:" + this.token + "@" + url;
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "-C", destDir, "clone", cloneURL);
            pb.redirectErrorStream(false);
            Process p = pb.start();
            p.waitFor();

            if (p.exitValue() != 0) {
                System.err.println("There was an error while trying to clone the repo. Maybe your url is invalid. Response code: " + p.exitValue());
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}

