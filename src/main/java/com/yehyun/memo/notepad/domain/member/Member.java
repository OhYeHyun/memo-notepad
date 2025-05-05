package com.yehyun.memo.notepad.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

//CREATE TABLE member (
//        id BIGINT AUTO_INCREMENT PRIMARY KEY,
//        name VARCHAR(100) NOT NULL,
//        login_id VARCHAR(50) NOT NULL UNIQUE,
//        password VARCHAR(255) NOT NULL
//);

@Slf4j
@Data
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String loginId;
    private String password;

    public Member() {
    }

    public Member(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }

    public boolean isPasswordMatch(String rawPassword) {
        log.info("password={}, rawPassword={}", password, rawPassword);

        return password.equals(rawPassword);
    }
}
