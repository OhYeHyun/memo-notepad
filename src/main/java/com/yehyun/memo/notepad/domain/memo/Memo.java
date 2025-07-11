package com.yehyun.memo.notepad.domain.memo;

import com.yehyun.memo.notepad.domain.member.Member;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "memo")
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @Column(name = "content")
    private String content;
    @Column(name = "is_checked")
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public Memo() {
    }

    public Memo(String content, Member member) {
        this.content = content;
        this.isChecked = false;
        this.member = member;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void toggleCheck() {
        this.isChecked = !this.isChecked;
    }

    public void updateWriter(Member member) {
        this.member = member;
    }
}
