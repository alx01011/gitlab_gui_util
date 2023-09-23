package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
      "group_access": {
        "access_level": 50,
        "notification_level": 3
      }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupAccess {
    private int access_level;
    private int notification_level;
}
