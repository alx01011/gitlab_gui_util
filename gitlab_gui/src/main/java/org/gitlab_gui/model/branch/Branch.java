package org.gitlab_gui.model.branch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gitlab_gui.model.commit.Commit;

/*
    {
        "name": "bname",
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
            "trailers": {},
            "web_url": "https://gitlab.com/bea397ea80a9ffba311"
        },
        "merged": false,
        "protected": false,
        "developers_can_push": false,
        "developers_can_merge": false,
        "can_push": true,
        "default": false,
        "web_url": "https://gitlab.com/forms"
    }
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    private String name;
    private Commit commit;
    private boolean merged;
    private boolean protectedBranch;
    private boolean developersCanPush;
    private boolean developersCanMerge;
    private boolean canPush;
    private boolean isDefault;
    private String webUrl;

}
