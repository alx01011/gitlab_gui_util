package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
    "permissions": {
        "project_access": {
            "access_level": 30,
            "notification_level": 3
        },
        "group_access": null
    }
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPermissions {
    private ProjectAccess project_access;
    private GroupAccess group_access;
}
