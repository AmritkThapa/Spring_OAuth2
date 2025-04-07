package com.amrit.SpringOauth2.util;

import com.amrit.SpringOauth2.entity.User;
import com.amrit.SpringOauth2.entity.UserToken;
import com.amrit.SpringOauth2.repository.UserTokenRepository;

import java.util.Optional;

public class UserTokenUtil {
    public static UserToken saveToken(User user, String accessToken, UserTokenRepository tokenRepo, String refreshToken) {
        UserToken token = new UserToken();
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        return tokenRepo.save(token);
    }
    public static void invalidateRefreshToken(String refreshToken, UserTokenRepository userTokenRepository) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken);
        if (userToken!=null) {
            userToken.setLoggedOut(true);
            userTokenRepository.save(userToken);
        }
    }
}
