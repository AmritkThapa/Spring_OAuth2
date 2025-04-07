package com.amrit.SpringOauth2.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    private String authToken;
    private String refreshToken;
}
