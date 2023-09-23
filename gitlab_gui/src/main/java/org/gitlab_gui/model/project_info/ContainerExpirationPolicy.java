package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
    "container_expiration_policy": {
        "cadence": "1d",
        "enabled": false,
        "keep_n": 10,
        "older_than": "90d",
        "name_regex": ".*",
        "name_regex_keep": null,
        "next_run_at": "2023-03-11T07:58:38.316Z"
    }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerExpirationPolicy {
    private String cadence;
    private boolean enabled;
    private int keep_n;
    private String older_than;
    private String name_regex;
    private String name_regex_keep;
    private String next_run_at;
}
