package com.yehyun.memo.notepad.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JwtPrincipal implements UserDetails, OAuth2User {

    private final String name;
    private final String loginId;
    private final String role;
    private final Map<String, Object> attributes;

    public JwtPrincipal(String name, String loginId, String role) {
        this.name = name;
        this.loginId = loginId;
        this.role = role;
        this.attributes = Map.of();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
