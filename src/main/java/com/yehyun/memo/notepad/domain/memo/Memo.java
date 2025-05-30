package com.yehyun.memo.notepad.domain.memo;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private boolean isChecked;
    private String writerId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    public Memo() {
    }

    public Memo(String content, String writerId) {
        this.content = content;
        this.isChecked = false;
        this.writerId = writerId;
    }

    public void update(String content) {
        this.content = content;
    }

    public void toggleCheck() {
        this.isChecked = !this.isChecked;
    }
}
