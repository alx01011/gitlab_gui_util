package org.gitlab_gui.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    "created_by": {
      "id": 1,
      "username": "raymond_smith",
      "name": "Raymond Smith",
      "state": "active",
      "avatar_url": "https://www.gravatar.com/avatar/c2525a7f58ae3776070e44c106c48e15?s=80&d=identicon",
      "web_url": "http://192.168.1.8:3000/root"
    }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatedBy {
    private int id;
    private String username;
    private String name;
    private String state;
    private String avatar_url;
    private String web_url;
}
