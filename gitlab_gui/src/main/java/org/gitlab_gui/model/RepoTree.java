package org.gitlab_gui.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepoTree {
    private String id;
    private String name;
    private String type;
    private String path;
    private String mode;

    public String toString() {
        return """
                id: %s
                name: %s
                type: %s
                path: %s
                mode: %s
                """ .formatted(id, name, type, path, mode);
    }
}
