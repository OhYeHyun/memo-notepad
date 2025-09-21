package com.notepad.memo.repository.impl;

import com.notepad.entity.QMemo;
import com.notepad.global.enums.SortBy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
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

    public List<Memo> searchMemosFast(MemoSearchRequest request, Long memberId) {
        QMemo memo = QMemo.memo;

        BooleanBuilder where = new BooleanBuilder();
        where.and(memo.writerId.eq(memberId));

        if (request.from() != null) {
            where.and(memo.createdAt.goe((request.from().atStartOfDay())));
        }
        if (request.to() != null) {
            where.and(memo.createdAt.lt((request.to().atStartOfDay())));
        }

        if (request.q() != null && !request.q().isBlank()) {
            where.and(memo.content.startsWithIgnoreCase(request.q()));
        }

        OrderSpecifier<?> order = SortBy.UPDATED.equals(request.sortBy()) ? memo.updatedAtContent.desc() : memo.createdAt.desc();

        return queryFactory.selectFrom(memo)
                .where(where)
                .orderBy(order, memo.id.desc())
                .limit(20)
                .fetch();
    }
}

