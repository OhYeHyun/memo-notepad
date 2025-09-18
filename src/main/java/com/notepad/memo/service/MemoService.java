package com.notepad.memo.service;

import com.notepad.entity.User;
import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.user.repository.UserRepository;
import com.notepad.memo.repository.MemoRepository;
import com.notepad.memo.repository.impl.MemoRepositorySupport;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final MemoRepositorySupport memoRepositorySupport;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Memo> getAllMemos(@Nullable MemoSearchRequest cond, Long userId) {
        if (cond == null) {
            return memoRepository.findMemos(userId);
        }
        return memoRepositorySupport.searchMemos(cond, userId);
    }

    @Transactional
    public Memo saveMemo(String content, Long userId) {
        User writer = userRepository.findById(userId).orElseThrow();
        Memo memo = new Memo(content, writer.getId());

        return memoRepository.save(memo);
    }

    @Transactional
    public void toggleMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memo.toggleCheck();
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memoRepository.delete(memo);
    }

    @Transactional
    public void updateMemo(Long id, String content) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.updateContent(content);
    }

    @Transactional
    public void updateWriter(Long guestId, Long userId) {
        List<Memo> guestMemoList = memoRepository.findMemos(guestId);
        User user = userRepository.findById(userId).orElseThrow();

        guestMemoList.forEach(memo -> memo.updateWriter(user.getId()));
    }

    @Transactional(readOnly = true)
    public Memo findById(Long memoId) {
        return memoRepository.findById(memoId).orElseThrow();
    }
}
