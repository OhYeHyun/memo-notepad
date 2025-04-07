package com.yehyun.memo.notepad.repository;

import com.yehyun.memo.notepad.domain.Memo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoRepository {

    private static final Map<Long, Memo> store = new ConcurrentHashMap<>();
    private static long sequence = 0L;

    public void save(Memo memo) {
        memo.setId(++sequence);
        store.put(memo.getId(), memo);
    }

    public void delete(Memo memo) {
        store.remove(memo.getId());
    }

    public Memo findById(Long id) {
        return store.get(id);
    }

    public List<Memo> getAll() {
        return new ArrayList<>(store.values());
    }
}
