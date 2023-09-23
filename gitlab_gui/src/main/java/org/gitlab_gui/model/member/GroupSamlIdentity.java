package org.gitlab_gui.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    "group_saml_identity": {
      "extern_uid":"ABC-1234567890",
      "provider": "group_saml",
      "saml_provider_id": 10
    }
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupSamlIdentity {
    private String extern_uid;
    private String provider;
    private int saml_provider_id;
}
