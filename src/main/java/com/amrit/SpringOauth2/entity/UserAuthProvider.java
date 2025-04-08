package com.amrit.SpringOauth2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_auth_provider")
public class UserAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(name= "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name= "auth_provider_id")
    private String authProviderId;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "uuid")
    private User user;
}
