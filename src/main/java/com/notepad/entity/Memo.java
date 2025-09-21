package com.notepad.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "memo")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "is_checked")
    private Boolean isChecked = false;

    @Column(name = "writer_id")
    private Long writerId;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at_all", nullable = false)
    private LocalDateTime updatedAtAll;

    @Column(name = "updated_at_content", nullable = false)
    private LocalDateTime updatedAtContent;


    @Builder
    public Memo(String content, Long writerId) {
        this.content = content;
        this.writerId = writerId;
        this.updatedAtContent = LocalDateTime.now();
    }

    public void updateContent(String content) {
        if (this.content.equals(content)) {
            return;
        }

        this.content = content;
        this.updatedAtContent = LocalDateTime.now();
    }

    public void toggleCheck() {
        this.isChecked = !this.isChecked;
    }

    public void updateWriter(Long writerId) {
        this.writerId = writerId;
    }
}
