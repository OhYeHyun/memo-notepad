package com.notepad.auth.dto;

import com.notepad.entity.User;
import com.notepad.global.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalUser implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public PrincipalUser(User user) {
        this.user = user;
        this.attributes = Map.of();
    }

    public PrincipalUser(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> user.getRole().name());

        return collection;
    }

    public Long getId() {
        return user.getId();
    }

    // OAuth
    @Override
    public String getName() {
        return user.getName();
    }

    // UserDetails
    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public String getPassword() {
        return user.getPassword() != null ? user.getPassword() : "";
    }

    public Role getRole() {
        return user.getRole();
    }
}
