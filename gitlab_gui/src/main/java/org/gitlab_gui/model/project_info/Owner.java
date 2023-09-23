package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
    "owner": {
        "id": 22222,
        "username": "asdasd",
        "name": "asdasd",
        "state": "active",
        "avatar_url": "https://gitlab.com/uploads/-/system/user/avatar/222/avatar.png",
        "web_url": "https://gitlab.com/asdasda"
    }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Owner {
    private int id;
    private String username;
    private String name;
    private String state;
    private String avatar_url;
    private String web_url;
}
