package com.notepad.memo.service;

import com.notepad.dto.request.memo.MemoCreateRequest;
import com.notepad.dto.response.memo.MemoClientResponse;
import com.notepad.entity.User;
import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.user.repository.UserRepository;
import com.notepad.memo.repository.MemoRepository;
import com.notepad.memo.repository.impl.MemoRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.notepad.dto.response.memo.MemoClientResponse.ofMemo;
import static com.notepad.dto.response.memo.MemoClientResponse.ofMemos;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final MemoRepositorySupport memoRepositorySupport;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MemoClientResponse> getMemoList(Long userId, MemoSearchRequest request) {
        List<Memo> memoList = memoRepositorySupport.searchMemos(request, userId);

        return ofMemos(memoList);
    }

    @Transactional
    public MemoClientResponse saveMemo(Long userId, MemoCreateRequest request) {
        userRepository.findById(userId).orElseThrow();

        Memo memo = memoRepository.save(
                Memo.builder()
                        .content(request.content())
                        .writerId(userId)
                        .build()
        );

        return ofMemo(memo);
    }

    @Transactional
    public MemoClientResponse toggleMemo(Long userId, Long memoId) {
        memoRepository.findById(userId).orElseThrow();

        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memo.toggleCheck();

        return ofMemo(memo);
    }

    @Transactional
    public MemoClientResponse updateMemo(Long userId, Long memoId, MemoCreateRequest request) {
        userRepository.findById(userId).orElseThrow();

        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memo.updateContent(request.content());

        return ofMemo(memo);
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memoRepository.delete(memo);
    }

    @Transactional
    public void updateWriter(Long guestId, Long userId) {
        List<Memo> guestMemoList = memoRepository.findMemos(guestId);
        User user = userRepository.findById(userId).orElseThrow();

        guestMemoList.forEach(memo -> memo.updateWriter(user.getId()));
    }
}
