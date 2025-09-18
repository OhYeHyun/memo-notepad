package com.yehyun.memo.notepad.repository;

import com.yehyun.memo.notepad.domain.memo.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @Query("select m from Memo m where m.writerId = :userId order by m.createdDate")
    List<Memo> findMemos(Long userId);
}
