package com.yehyun.memo.notepad.security.dto;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalMember implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;

    public PrincipalMember(Member member) {
        this.member = member;
        this.attributes = Map.of();
    }

    public PrincipalMember(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> member.getRole().getValue());

        return collection;
    }

    public String getNickname() {
        return member.getName();
    }

    // OAuth
    @Override
    public String getName() {
        return member.getLoginId();
    }

    // UserDetails
    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    public String getPassword() {
        return member.getPassword() != null ? member.getPassword() : "";
    }

    public Role getRole() {
        return member.getRole();
    }
}
