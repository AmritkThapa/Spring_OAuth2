package com.amrit.SpringOauth2.core.oauth;

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
import com.amrit.SpringOauth2.util.ResponseUtil;
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
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserTokenRepository userTokenRepository;

    private final ObjectMapper objectMapper;
    private final UserAuthProviderRepository userAuthProviderRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtService jwtService, UserTokenRepository userTokenRepository, ObjectMapper objectMapper, UserAuthProviderRepository userAuthProviderRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userTokenRepository = userTokenRepository;
        this.objectMapper = objectMapper;
        this.userAuthProviderRepository = userAuthProviderRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        OAuthUserModel oauthUserModel = getUserFromOAuth2User(oAuth2User);
        oauthUserModel.setAuthProvider(oauthToken.getAuthorizedClientRegistrationId().toUpperCase());
        AuthProvider provider = AuthProvider.valueOf(oauthUserModel.getAuthProvider());
        try {
            if (provider == AuthProvider.GOOGLE){
                oauthUserModel.setProfileImage(oAuth2User.getAttribute("picture"));
                oauthUserModel.setAuthProviderId(oAuth2User.getAttribute("sub"));
            }
            else if (provider == AuthProvider.FACEBOOK) {
                oauthUserModel.setAuthProviderId(oAuth2User.getAttribute("id"));
                Map<String, Object> pictureObj = oAuth2User.getAttribute("picture");
                if (pictureObj != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                    if (dataObj != null) {
                        String pictureUrl = (String) dataObj.get("url");
                        oauthUserModel.setProfileImage(pictureUrl);
                    }
                }
            }
        }
        catch (IllegalArgumentException e){
            ApiResponse<?> apiResponse = ResponseUtil.getFailureResponse("Unsupported authentication provider");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        Optional<User> optionalUser = userRepository.findByEmail(oauthUserModel.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            UserAuthProvider existingUserAuthProvider = userAuthProviderRepository.findByUserEmailAndAuthProvider(oauthUserModel.getEmail(), provider);
            if (existingUserAuthProvider!=null){
                user.setProfileImage(oauthUserModel.getProfileImage());
                user.setName(oauthUserModel.getName());
                userRepository.save(user);
            }
            else {
                UserAuthProvider userAuthProvider = saveExistingUser(oauthUserModel, user);
                userAuthProviderRepository.save(userAuthProvider);
            }
        } else {
            user = registerUser(oauthUserModel);
            userRepository.save(user);
            UserAuthProvider userAuthProvider = saveExistingUser(oauthUserModel, user);
            userAuthProviderRepository.save(userAuthProvider);
        }
        UserToken userToken = UserTokenUtil.saveToken(user,
                jwtService.generateAccessToken(user),
                userTokenRepository,
                jwtService.generateRefreshToken(user));
        response.setContentType("application/json");
        ApiResponse<?> apiResponse =  generateResponse(userToken);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private User registerUser(OAuthUserModel oauthUserModel) {
        User user = new User();
        user.setEmail(oauthUserModel.getEmail());
        user.setName(oauthUserModel.getName());
        user.setProfileImage(oauthUserModel.getProfileImage());
        return user;
    }

    private UserAuthProvider saveExistingUser(OAuthUserModel oauthUserModel, User user) {
        UserAuthProvider userAuthProvider = new UserAuthProvider();
        userAuthProvider.setUser(user);
        userAuthProvider.setAuthProvider(AuthProvider.valueOf(oauthUserModel.getAuthProvider()));
        userAuthProvider.setAuthProviderId(oauthUserModel.getAuthProviderId());
        return userAuthProvider;
    }

    private OAuthUserModel getUserFromOAuth2User(OAuth2User oAuth2User) {
        OAuthUserModel oAuthUserModel = new OAuthUserModel();
        oAuthUserModel.setEmail(oAuth2User.getAttribute("email"));
        oAuthUserModel.setName(oAuth2User.getAttribute("name"));
        return oAuthUserModel;
    }
    private ApiResponse<?> generateResponse(UserToken userToken) {
        AuthenticationResponse responseBody = new AuthenticationResponse();
        responseBody.setAuthToken(userToken.getAccessToken());
        responseBody.setRefreshToken(userToken.getRefreshToken());
        return ResponseUtil.getSuccessfulApiResponse(responseBody, "Login successful");
    }
}
