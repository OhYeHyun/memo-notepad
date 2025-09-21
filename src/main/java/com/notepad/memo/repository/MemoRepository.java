package com.notepad.memo.repository;

import com.notepad.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @Query("select m from Memo m where m.writerId = :userId")
    List<Memo> findMemos(Long userId);

    @Query(value = """
            SELECT m.*
              FROM memo m
             WHERE m.writer_id = :writerId
               AND MATCH(m.content) AGAINST (:fulltext IN BOOLEAN MODE)
             ORDER BY
              CASE WHEN :sortBy = 'UPDATED' THEN m.updated_at_content END DESC,
              CASE WHEN :sortBy = 'CREATED' THEN m.created_at END DESC,
              m.memo_id DESC
            LIMIT 20
    """, nativeQuery = true)
    List<Memo> searchFulltext(Long writerId, String fulltext, String sortBy);
}
