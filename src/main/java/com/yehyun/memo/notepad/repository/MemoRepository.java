package com.yehyun.memo.notepad.repository;

import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSearchCond;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemoRepository {

    private final EntityManager em;

    public Memo save(Memo memo) {
        em.persist(memo);
        return memo;
    }

    public void delete(Memo memo) {
        em.remove(memo);
    }

    public Optional<Memo> findById(Long id) {
        Memo memo = em.find(Memo.class, id);
        return Optional.ofNullable(memo);
    }

    public List<Memo> findMemoList(Long memberId) {
        String jpql = "select m from Memo m where m.member.id = :memberId";
        return em.createQuery(jpql, Memo.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Memo> searchMemos(MemoSearchCond cond, Long memberId) {
        StringBuilder jpql = new StringBuilder("select m from Memo m where m.member.id = :memberId");
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);

        if (cond.getContent() != null && !cond.getContent().isEmpty()) {
            jpql.append(" AND m.content LIKE :content");
            params.put("content", "%" + cond.getContent() + "%");
        }

        if (cond.getCreatedDate() != null) {
            jpql.append(" AND m.createdDate >= :createdDate");
            params.put("createdDate", cond.getCreatedDate());
        }

        TypedQuery<Memo> query = em.createQuery(jpql.toString(), Memo.class);
        params.forEach((key, value) -> query.setParameter(key, value));

        return query.getResultList();
    }

}
