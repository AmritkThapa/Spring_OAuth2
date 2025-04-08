package com.amrit.SpringOauth2.service.impl;

import com.amrit.SpringOauth2.dto.request.AuthenticationRequest;
import com.amrit.SpringOauth2.dto.response.ApiResponse;
import com.amrit.SpringOauth2.dto.response.AuthenticationResponse;
import com.amrit.SpringOauth2.entity.AuthProvider;
import com.amrit.SpringOauth2.entity.User;
import com.amrit.SpringOauth2.entity.UserAuthProvider;
import com.amrit.SpringOauth2.entity.UserToken;
import com.amrit.SpringOauth2.repository.UserAuthProviderRepository;
import com.amrit.SpringOauth2.repository.UserRepository;
import com.amrit.SpringOauth2.repository.UserTokenRepository;
import com.amrit.SpringOauth2.core.service.JwtService;
import com.amrit.SpringOauth2.service.AuthenticationService;
import com.amrit.SpringOauth2.util.ResponseUtil;
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
    private final UserAuthProviderRepository userAuthProviderRepository;

    public AuthenticationServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, UserTokenRepository userTokenRepository, UserAuthProviderRepository userAuthProviderRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userTokenRepository = userTokenRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
    }

    @Override
    public ApiResponse<?> authenticate(AuthenticationRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            UserAuthProvider userAuthProvider = userAuthProviderRepository.findByUserEmailAndAuthProvider(
                            request.getEmail(), AuthProvider.SHIROE);
            if (userAuthProvider!=null) {
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
                        return ResponseUtil.getSuccessfulApiResponse(response, "User authenticated successfully");
                    } else {
                        return ResponseUtil.getFailureResponse("Authentication failed");
                    }
                } catch (Exception e) {
                    return ResponseUtil.getFailureResponse("Authentication failed");
                }
            }
        }
        return ResponseUtil.getNotFoundResponse("User not found");
    }

    @Override
    public AuthenticationResponse hello() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAuthToken("Hello");
        response.setRefreshToken("World");
        return response;
    }
}
