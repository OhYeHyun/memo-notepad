package com.yehyun.memo.notepad.domain.memo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//CREATE TABLE memo (
//       id BIGINT AUTO_INCREMENT PRIMARY KEY,
//       content TEXT NOT NULL,
//       is_checked BOOLEAN NOT NULL DEFAULT FALSE,
//       writer_id VARCHAR(255) NOT NULL
//);

@Data
@Entity
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private boolean isChecked;
    private String writerId;

    public Memo() {
    }

    public Memo(String content, String writerId) {
        this.content = content;
        this.isChecked = false;
        this.writerId = writerId;
    }

    public void update(String content, boolean isChecked) {
        this.content = content;
        this.isChecked = isChecked;
    }

    public void toggleCheck() {
        this.isChecked = !this.isChecked;
    }
}
