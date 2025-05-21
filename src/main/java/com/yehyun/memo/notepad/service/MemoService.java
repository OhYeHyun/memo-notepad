package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {

    private final MemoRepository memoRepository;

    public List<Memo> getAllMemos(String writerId) {
        return memoRepository.findAll(writerId)
                .stream()
                .sorted(Comparator.comparing(Memo::getCreatedDate))
                .collect(Collectors.toList());
    }

    public Memo saveMemo(String content, String writerId) {
        Memo memo = new Memo(content, writerId);
        return memoRepository.save(memo);
    }

    public void toggleMemo(Long id) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.toggleCheck();
    }

    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        memoRepository.delete(memo);
    }

    public Memo findById(Long id) {
        return memoRepository.findById(id).orElseThrow();
    }

    public void updateMemo(Long id, String content) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.update(content);
    }

    public void updateWriterId(String guestId, String loginId) {
        memoRepository.updateWriterId(guestId, loginId);
    }
}
