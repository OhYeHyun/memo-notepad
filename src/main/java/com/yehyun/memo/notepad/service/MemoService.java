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

    public List<Memo> getAllMemos(Long loginMemberId, String guestId) {
        String writerId = decideWriterId(loginMemberId, guestId);

        return memoRepository.findAll(writerId)
                .stream()
                .sorted(Comparator.comparing(Memo::getCreatedDate))
                .collect(Collectors.toList());
    }

    public Memo saveMemo(String content, Long loginMemberId, String guestId) {
        String writerId = decideWriterId(loginMemberId, guestId);

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

    private String decideWriterId(Long loginMemberId, String guestId) {
        return (loginMemberId != null) ? String.valueOf(loginMemberId) : guestId;
    }
}
