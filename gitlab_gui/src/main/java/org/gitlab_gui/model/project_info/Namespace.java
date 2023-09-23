package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
    "namespace": {
      "id": 3,
      "name": "Diaspora",
      "path": "diaspora",
      "kind": "group",
      "full_path": "diaspora",
      "parent_id": null,
      "avatar_url": "https://gitlab.example.com/uploads/project/avatar/6/uploads/avatar.png",
      "web_url": "https://gitlab.example.com/diaspora"
    },
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Namespace {
    private int id;
    private String name;
    private String path;
    private String kind;
    private String full_path;
    private String parent_id;
    private String avatar_url;
    private String web_url;
}
