drop table if exists member CASCADE;
drop table if exists memo CASCADE;

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE memo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    is_checked BOOLEAN NOT NULL DEFAULT FALSE,
    writer_id VARCHAR(255) NOT NULL
);