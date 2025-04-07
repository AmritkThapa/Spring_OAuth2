package com.amrit.SpringOauth2.service.impl;

import com.amrit.SpringOauth2.dto.request.AuthenticationRequest;
import com.amrit.SpringOauth2.dto.response.AuthenticationResponse;
import com.amrit.SpringOauth2.entity.User;
import com.amrit.SpringOauth2.entity.UserToken;
import com.amrit.SpringOauth2.repository.UserRepository;
import com.amrit.SpringOauth2.repository.UserTokenRepository;
import com.amrit.SpringOauth2.core.service.JwtService;
import com.amrit.SpringOauth2.service.AuthenticationService;
import com.amrit.SpringOauth2.util.UserTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserTokenRepository userTokenRepository;

    public AuthenticationServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, UserTokenRepository userTokenRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
                authentication.isAuthenticated();
                if (authentication.isAuthenticated()) {
                    User user = optionalUser.get();
                    UserToken userToken = UserTokenUtil.saveToken(user,
                            jwtService.generateAccessToken(user),
                            userTokenRepository,
                            jwtService.generateRefreshToken(user));
                    AuthenticationResponse response = new AuthenticationResponse();
                    response.setAuthToken(userToken.getAccessToken());
                    response.setRefreshToken(userToken.getRefreshToken());
                    return response;
                } else {
                    throw new RuntimeException("Authentication failed");
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Authentication failed", e);
            }
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public AuthenticationResponse hello() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAuthToken("Hello");
        response.setRefreshToken("World");
        return response;
    }
}
