package com.notepad.memo.repository;

import com.notepad.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @Query("select m from Memo m where m.writerId = :userId")
    List<Memo> findMemos(Long userId);

    @Query(value = """
        SELECT m.memo_id
          FROM memo m
         WHERE MATCH(m.content) AGAINST(:fulltext IN BOOLEAN MODE)
         LIMIT 3000
    """, nativeQuery = true)
    List<Long> findFulltextCandidateIds(String fulltext);
}
