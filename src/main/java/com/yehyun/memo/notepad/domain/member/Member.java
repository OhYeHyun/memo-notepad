package com.yehyun.memo.notepad.domain.member;

import com.yehyun.memo.notepad.security.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    public Member() {
    }

    private Member(String name, String loginId, String password, Role role) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    private Member(String name, String loginId, String password, Role role, String provider, String providerId) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static Member ofLocal(String name, String loginId, String password, Role role) {
        return new Member(name, loginId, password, role);
    }

    public static Member ofOAuth(String name, String loginId, String password, Role role, String provider, String providerId) {
        return new Member(name, loginId, password, role, provider, providerId);
    }
}
