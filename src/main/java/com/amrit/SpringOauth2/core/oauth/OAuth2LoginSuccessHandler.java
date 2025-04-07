package com.amrit.SpringOauth2.core.oauth;

import com.amrit.SpringOauth2.dto.response.AuthenticationResponse;
import com.amrit.SpringOauth2.entity.AuthProvider;
import com.amrit.SpringOauth2.entity.User;
import com.amrit.SpringOauth2.entity.UserToken;
import com.amrit.SpringOauth2.repository.UserRepository;
import com.amrit.SpringOauth2.repository.UserTokenRepository;
import com.amrit.SpringOauth2.core.service.JwtService;
import com.amrit.SpringOauth2.util.UserTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserTokenRepository userTokenRepository;

    private final ObjectMapper objectMapper;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService, UserTokenRepository userTokenRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userTokenRepository = userTokenRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profileImage = oAuth2User.getAttribute("picture");
        String authProvider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        String oauthId = "";
        try {
            AuthProvider provider = AuthProvider.valueOf(authProvider);
            if (provider == AuthProvider.GOOGLE){
                oauthId = oAuth2User.getAttribute("sub");
            }
            else if (provider == AuthProvider.FACEBOOK) {
                oauthId = oAuth2User.getAttribute("id");
            }
        }
        catch (IllegalArgumentException e){
            log.info("Unsupported auth provider: {}", authProvider);
            return;
        }


        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProfileImage(profileImage);
            user.setAuthProviderId(oauthId);
            user.setAuthProvider(AuthProvider.valueOf(authProvider));
            userRepository.save(user);
        }
        UserToken userToken = UserTokenUtil.saveToken(user,
                jwtService.generateAccessToken(user),
                userTokenRepository,
                jwtService.generateRefreshToken(user));
        AuthenticationResponse responseBody = new AuthenticationResponse();
        responseBody.setAuthToken(userToken.getAccessToken());
        responseBody.setRefreshToken(userToken.getRefreshToken());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
