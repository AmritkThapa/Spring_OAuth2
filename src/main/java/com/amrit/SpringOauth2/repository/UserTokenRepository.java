package com.amrit.SpringOauth2.repository;

import com.amrit.SpringOauth2.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    UserToken findByAccessToken(String token);

    UserToken findByRefreshToken(String token);
}
