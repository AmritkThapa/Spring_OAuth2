package com.amrit.SpringOauth2.service;

import com.amrit.SpringOauth2.dto.request.AuthenticationRequest;
import com.amrit.SpringOauth2.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse hello();
}
