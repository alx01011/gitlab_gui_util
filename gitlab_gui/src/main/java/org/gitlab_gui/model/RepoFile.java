package org.gitlab_gui.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepoFile {
    String file_name;
    String file_path;
    int size;
    String encoding;
    String content;
    String content_sha256;
    String ref;
    String blob_id;
    String commit_id;
    String last_commit_id;
    boolean execute_filemode;
}
