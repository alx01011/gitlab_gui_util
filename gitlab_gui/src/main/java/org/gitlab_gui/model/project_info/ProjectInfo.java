package org.gitlab_gui.model.project_info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/*
{
    "id": 1763,
    "description": "Assignment 1:  ()",
    "name": "hy255_ask1",
    "name_with_namespace": ",
    "path": "assignment1_1",
    "path_with_namespace": "/assignment1_1",
    "created_at": "2021-02-15T19:43:58.002+02:00",
    "default_branch": "master",
    "tag_list": [],
    "ssh_url_to_repo": "git@,
    "http_url_to_repo": "https://assignment1_1.git",
    "web_url": "https://assignment1_1",
    "readme_url": "https:/signment1_1/-/blob/master/README.md",
    "avatar_url": null,
    "forks_count": 0,
    "star_count": 0,
    "last_activity_at": "2023-09-22T01:37:32.280+03:00",
    "namespace": {
        "id": 450,
        "name": "",
        "path": "",
        "kind": "user",
        "full_path": "",
        "parent_id": null,
        "avatar_url": "/uploads/-/system/user/avatar/412/avatar.png",
        "web_url": "https://..gr/"
    },
    "_links": {

    },
    "packages_enabled": null,
    "empty_repo": false,
    "archived": false,
    "visibility": "private",
    "owner": {
        "id": 412,
        "name": "A",
        "username": "",
        "state": "active",
        "avatar_url": "https://../uploads/-/system/user/avatar/412/avatar.png",
        "web_url": "https://./"
    },
    "resolve_outdated_diff_discussions": false,
    "container_registry_enabled": true,
    "container_expiration_policy": {
        "cadence": "7d",
        "enabled": true,
        "keep_n": null,
        "older_than": null,
        "name_regex": null,
        "name_regex_keep": null,
        "next_run_at": "2021-09-07T13:50:25.634+03:00"
    },
    "issues_enabled": true,
    "merge_requests_enabled": true,
    "wiki_enabled": true,
    "jobs_enabled": false,
    "snippets_enabled": true,
    "service_desk_enabled": false,
    "service_desk_address": null,
    "can_create_merge_request_in": true,
    "issues_access_level": "enabled",
    "repository_access_level": "enabled",
    "merge_requests_access_level": "enabled",
    "forking_access_level": "enabled",
    "wiki_access_level": "enabled",
    "builds_access_level": "disabled",
    "snippets_access_level": "enabled",
    "pages_access_level": "enabled",
    "operations_access_level": "enabled",
    "analytics_access_level": "enabled",
    "emails_disabled": null,
    "shared_runners_enabled": true,
    "lfs_enabled": true,
    "creator_id": 412,
    "import_status": "finished",
    "import_error": null,
    "open_issues_count": 0,
    "runners_token": "",
    "ci_default_git_depth": 50,
    "ci_forward_deployment_enabled": true,
    "public_jobs": true,
    "build_git_strategy": "fetch",
    "build_timeout": 3600,
    "auto_cancel_pending_pipelines": "enabled",
    "build_coverage_regex": null,
    "ci_config_path": null,
    "shared_with_groups": [],
    "only_allow_merge_if_pipeline_succeeds": false,
    "allow_merge_on_skipped_pipeline": null,
    "restrict_user_defined_variables": false,
    "request_access_enabled": true,
    "only_allow_merge_if_all_discussions_are_resolved": false,
    "remove_source_branch_after_merge": true,
    "printing_merge_request_link_enabled": true,
    "merge_method": "merge",
    "suggestion_commit_message": null,
    "auto_devops_enabled": true,
    "auto_devops_deploy_strategy": "continuous",
    "autoclose_referenced_issues": true,
    "permissions": {
        "project_access": {
            "access_level": 40,
            "notification_level": 3
        },
        "group_access": null
    }
}

 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInfo {
    private int id;
    private String description;
    private String name;
    private String name_with_namespace;
    private String path;
    private String path_with_namespace;
    private String created_at;
    private String default_branch;
    private String[] tag_list;
    private String ssh_url_to_repo;
    private String http_url_to_repo;
    private String web_url;
    private String readme_url;
    private String avatar_url;
    private int forks_count;
    private int star_count;
    private String last_activity_at;
    private Namespace namespace;
    private Links _links;
    private String packages_enabled;
    private boolean empty_repo;
    private boolean archived;
    private String visibility;
    private Owner owner;
    private boolean resolve_outdated_diff_discussions;
    private boolean container_registry_enabled;
    private ContainerExpirationPolicy container_expiration_policy;
    private boolean issues_enabled;
    private boolean merge_requests_enabled;
    private boolean wiki_enabled;
    private boolean jobs_enabled;
    private boolean snippets_enabled;
    private boolean service_desk_enabled;
    private String service_desk_address;
    private boolean can_create_merge_request_in;
    private String issues_access_level;
    private String repository_access_level;
    private String merge_requests_access_level;
    private String forking_access_level;
    private String wiki_access_level;
    private String builds_access_level;
    private String snippets_access_level;
    private String pages_access_level;
    private String operations_access_level;
    private String analytics_access_level;
    private String emails_disabled;
    private boolean shared_runners_enabled;
    private boolean lfs_enabled;
    private int creator_id;
    private String import_status;
    private String import_error;
    private int open_issues_count;
    private String runners_token;
    private int ci_default_git_depth;
    private boolean ci_forward_deployment_enabled;
    private boolean public_jobs;
    private String build_git_strategy;
    private int build_timeout;
    private String auto_cancel_pending_pipelines;
    private String build_coverage_regex;
    private String ci_config_path;
    private String[] shared_with_groups;
    private boolean only_allow_merge_if_pipeline_succeeds;
    private String allow_merge_on_skipped_pipeline;
    private boolean restrict_user_defined_variables;
    private boolean request_access_enabled;
    private boolean only_allow_merge_if_all_discussions_are_resolved;
    private boolean remove_source_branch_after_merge;
    private boolean printing_merge_request_link_enabled;
    private String merge_method;
    private String suggestion_commit_message;
    private boolean auto_devops_enabled;
    private String auto_devops_deploy_strategy;
    private boolean autoclose_referenced_issues;
    private ProjectPermissions permissions;
}
