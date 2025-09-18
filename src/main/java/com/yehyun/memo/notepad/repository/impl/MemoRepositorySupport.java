package com.yehyun.memo.notepad.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.QMemo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemoRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public List<Memo> searchMemos(MemoSearchCond cond, Long memberId) {
        QMemo memo = QMemo.memo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(memo.writerId.eq(memberId));

        if (cond.getContent() != null && !cond.getContent().isEmpty()) {
            builder.and(memo.content.contains(cond.getContent()));
        }

        if (cond.getCreatedDate() != null) {
            builder.and(memo.createdDate.goe(cond.getCreatedDate()));
        }

        return queryFactory.selectFrom(memo)
                .where(builder)
                .orderBy(memo.createdDate.desc())
                .fetch();
    }
}

