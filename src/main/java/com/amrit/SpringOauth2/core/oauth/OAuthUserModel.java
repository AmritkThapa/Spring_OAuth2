package com.amrit.SpringOauth2.core.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserModel {
    private String email;
    private String name;
    private String profileImage;
    private String authProvider;
    private String authProviderId;
}
