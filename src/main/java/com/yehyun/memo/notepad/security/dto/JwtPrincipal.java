package com.yehyun.memo.notepad.security.dto;

import com.yehyun.memo.notepad.security.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JwtPrincipal implements UserDetails, OAuth2User {

    private final String name;
    private final String loginId;

    @Enumerated(EnumType.STRING)
    private final Role role;
    private final Map<String, Object> attributes;

    public JwtPrincipal(String name, String loginId, Role role) {
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
        return List.of((GrantedAuthority) () -> role.name());
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
