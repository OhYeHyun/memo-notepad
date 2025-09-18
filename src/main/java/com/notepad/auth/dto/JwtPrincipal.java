package com.notepad.auth.dto;

import com.notepad.global.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JwtPrincipal implements UserDetails, OAuth2User {

    private final Long id;
    private final String name;

    @Enumerated(EnumType.STRING)
    private final Role role;
    private final Map<String, Object> attributes;

    public JwtPrincipal(Long id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.attributes = Map.of();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) role::name);
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
