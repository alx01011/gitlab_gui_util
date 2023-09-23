package org.gitlab_gui.model.project_info;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
    "_links": {
        "self": "https://gitlab.com/api/v4/projects/44202878",
        "issues": "https://gitlab.com/api/v4/projects/44202878/issues",
        "merge_requests": "https://gitlab.com/api/v4/projects/44202878/merge_requests",
        "repo_branches": "https://gitlab.com/api/v4/projects/44202878/repository/branches",
        "labels": "https://gitlab.com/api/v4/projects/44202878/labels",
        "events": "https://gitlab.com/api/v4/projects/44202878/events",
        "members": "https://gitlab.com/api/v4/projects/44202878/members",
        "cluster_agents": "https://gitlab.com/api/v4/projects/44202878/cluster_agents"
    },
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Links {
    private String self;
    private String issues;
    private String merge_requests;
    private String repo_branches;
    private String labels;
    private String events;
    private String members;
    private String cluster_agents;
}
