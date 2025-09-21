package com.notepad.memo.service;

import com.notepad.dto.request.memo.MemoCreateRequest;
import com.notepad.dto.response.memo.MemoClientResponse;
import com.notepad.entity.User;
import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.global.enums.SearchMode;
import com.notepad.global.enums.SortBy;
import com.notepad.user.repository.UserRepository;
import com.notepad.memo.repository.MemoRepository;
import com.notepad.memo.repository.impl.MemoRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.notepad.dto.response.memo.MemoClientResponse.ofMemo;
import static com.notepad.dto.response.memo.MemoClientResponse.ofMemos;
import static com.notepad.global.utils.FullText.toFTS;

@Service
@RequiredArgsConstructor
public class MemoService {

    private static final int MIN_FULLTEXT_LEN = 2;

    private final MemoRepository memoRepository;
    private final MemoRepositorySupport memoRepositorySupport;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MemoClientResponse> getMemoList(Long userId, MemoSearchRequest request) {
        String q = request.q();
        SearchMode mode = request.mode();
        SortBy sortBy = request.sortBy();

        if (sortBy == null) sortBy = SortBy.UPDATED;

        if (mode == null) {
            mode = (q != null && q.strip().length() >= MIN_FULLTEXT_LEN) ? SearchMode.FULLTEXT : SearchMode.FAST;
        }

        if (SearchMode.FULLTEXT.equals(mode)) {
            String fulltext = toFTS(request.q());
            System.out.println("========" + fulltext);

            if (fulltext != null) {
                System.out.println("====fullText====");
                return ofMemos(memoRepository.searchFulltext(userId, fulltext, sortBy.name()));
            }
        }
        System.out.println("====fast====");
        return ofMemos(
                memoRepositorySupport.searchMemosFast(
                        new MemoSearchRequest(q, SearchMode.FAST, sortBy, request.from(), request.to()),
                        userId
                )
        );
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
        userRepository.findById(userId).orElseThrow();

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
