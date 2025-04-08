package com.amrit.SpringOauth2.repository;

import com.amrit.SpringOauth2.entity.AuthProvider;
import com.amrit.SpringOauth2.entity.UserAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, String> {
    UserAuthProvider findByUserEmailAndAuthProvider(String userEmail, AuthProvider authProvider);
}
