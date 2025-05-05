package com.yehyun.memo.notepad.repository;

import com.yehyun.memo.notepad.domain.memo.Memo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public List<Memo> findAll() {
        String jpql = "select m from Memo m";
        return em.createQuery(jpql, Memo.class).getResultList();
    }
}
