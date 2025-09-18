package com.notepad.entity;

import com.notepad.global.enums.Role;
import com.notepad.auth.oauth.OAuth2Response;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    private User(String name, String loginId, String password, Role role) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    private User(String name, String loginId, String password, Role role, String provider, String providerId) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static User ofLocal(String name, String loginId, String password, Role role) {
        return new User(name, loginId, password, role);
    }

    public static User ofOAuth(String name, String loginId, String password, Role role, String provider, String providerId) {
        return new User(name, loginId, password, role, provider, providerId);
    }

    public void updateProvider(OAuth2Response response) {
        this.name = response.getName();
        this.provider = response.getProvider();
        this.providerId = response.getProviderId();
    }
}
