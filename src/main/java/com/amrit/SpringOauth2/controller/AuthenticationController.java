package com.amrit.SpringOauth2.controller;

import com.amrit.SpringOauth2.dto.request.AuthenticationRequest;
import com.amrit.SpringOauth2.dto.response.ApiResponse;
import com.amrit.SpringOauth2.dto.response.AuthenticationResponse;
import com.amrit.SpringOauth2.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ApiResponse<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.authenticate(authenticationRequest);
    }
    @GetMapping("/hello")
    public AuthenticationResponse hello(){
        return ResponseEntity.ok(authenticationService.hello()).getBody();
    }
}
