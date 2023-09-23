package org.gitlab_gui.model.commit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
        "commit": {
            "id": "asdasd",
            "short_id": "asdasdas",
            "created_at": "2023-07-04T09:39:37.000+00:00",
            "parent_ids": [
                "adasd",
                "asdsad"
            ],
            "title": "Merge branch a",
            "message": "asd",
            "author_name": "asdas",
            "author_email": "asd.com",
            "authored_date": "2023-07-04T09:39:37.000+00:00",
            "committer_name": "ss asdsa",
            "committer_email": "asd.com",
            "committed_date": "2023-07-04T09:39:37.000+00:00",
            "web_url": "https://gitlab.com/bea397ea80a9ffba311"
        }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Commit {
    private String id;
    private String shortId;
    private String createdAt;
    private String[] parentIds;
    private String title;
    private String message;
    private String authorName;
    private String authorEmail;
    private String authoredDate;
    private String committerName;
    private String committerEmail;
    private String committedDate;
    private String webUrl;
}
