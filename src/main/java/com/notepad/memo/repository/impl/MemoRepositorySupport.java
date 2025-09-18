package com.notepad.memo.repository.impl;

import com.notepad.entity.QMemo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemoRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public List<Memo> searchMemos(MemoSearchRequest request, Long memberId) {
        QMemo memo = QMemo.memo;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(memo.writerId.eq(memberId));

        if (request.content() != null && !request.content().isEmpty()) {
            builder.and(memo.content.contains(request.content()));
        }

        if (request.createdAt() != null) {
            builder.and(memo.createdAt.goe(request.createdAt()));
        }

        return queryFactory.selectFrom(memo)
                .where(builder)
                .orderBy(memo.createdAt.desc())
                .fetch();
    }
}

