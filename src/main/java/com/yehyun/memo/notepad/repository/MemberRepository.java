package com.yehyun.memo.notepad.repository;

import com.yehyun.memo.notepad.domain.member.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public Optional<Member> findByLoginId(String loginId) {
        String jpql = "select m from Member m where m.loginId = :loginId";

        List<Member> result = em.createQuery(jpql, Member.class)
                .setParameter("loginId", loginId)
                .getResultList();

        return result.stream().findFirst();
    }

    public List<Member> findAll() {
        String jpql = "select m from Member m";
        return em.createQuery(jpql, Member.class).getResultList();
    }
}
