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

    public List<Memo> searchMemosStartsWith(MemoSearchRequest request, Long memberId) {
        QMemo memo = QMemo.memo;

        BooleanBuilder where = baseConditions(request, memo, memberId);

        if (request.q() != null && !request.q().isBlank()) {
            where.and(memo.content.startsWith(request.q()));
        }

        return queryFactory.selectFrom(memo)
                .where(where)
                .orderBy(
                        sortOrder(request, memo),
                        memo.id.desc()
                )
                .limit(20)
                .fetch();
    }

    public List<Memo> searchMemosFulltext(MemoSearchRequest request, Long memberId, List<Long> candidateIds) {
        QMemo memo = QMemo.memo;

        BooleanBuilder where = baseConditions(request, memo, memberId)
                .and(memo.id.in(candidateIds));

        return queryFactory.selectFrom(memo)
                .where(where)
                .orderBy(
                        sortOrder(request, memo),
                        memo.id.desc()
                )
                .limit(20)
                .fetch();
    }

    private BooleanBuilder baseConditions(MemoSearchRequest request, QMemo memo, Long memberId) {
        BooleanBuilder where = new BooleanBuilder()
                .and(memo.writerId.eq(memberId));

        if (request.from() != null) {
            where.and(memo.createdAt.goe(request.from().atStartOfDay()));
        }
        if (request.to() != null) {
            where.and(memo.createdAt.lt(request.to().atStartOfDay()));
        }

        return where;
    }

    private OrderSpecifier<?> sortOrder(MemoSearchRequest request, QMemo memo) {
        if (request.sortBy().equals(SortBy.UPDATED)) {
            return memo.updatedAtContent.desc();
        }

        return memo.createdAt.desc();
    }
}

