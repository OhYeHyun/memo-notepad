package com.notepad.memo.service;

import com.notepad.dto.request.memo.MemoCreateRequest;
import com.notepad.dto.response.memo.MemoClientResponse;
import com.notepad.entity.User;
import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.global.enums.SearchMode;
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
        SearchMode mode = decideSearchMode(request.mode(), q);

        if (SearchMode.FULLTEXT.equals(mode)) {
            String fulltext = toFTS(q);

            if (fulltext != null) {
                List<Long> candidateIds = memoRepository.findFulltextCandidateIds(fulltext);
                System.out.println(candidateIds);

                if (candidateIds == null || candidateIds.isEmpty()) {
                    return List.of();
                }

                return ofMemos(memoRepositorySupport.searchMemosFulltext(request, userId, candidateIds));
            }
        }

        return ofMemos(memoRepositorySupport.searchMemosStartsWith(request, userId));
    }

    private SearchMode decideSearchMode(SearchMode requested, String q) {
        int len = q == null ? 0 : q.strip().length();

        if (requested == SearchMode.FULLTEXT && len >= MIN_FULLTEXT_LEN) {
            return SearchMode.FULLTEXT;
        }

        return SearchMode.STARTS_WITH;
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
