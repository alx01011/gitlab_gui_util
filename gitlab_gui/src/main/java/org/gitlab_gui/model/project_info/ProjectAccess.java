package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
      "project_access": {
        "access_level": 10,
        "notification_level": 3
      }
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAccess {
    private int access_level;
    private int notification_level;
}
