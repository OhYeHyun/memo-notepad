package com.yehyun.memo.notepad.service;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSearchCond;
import com.yehyun.memo.notepad.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    public List<Memo> getAllMemos(Long memberId) {
        return memoRepository.findMemoList(memberId)
                .stream()
                .sorted(Comparator.comparing(Memo::getCreatedDate))
                .collect(Collectors.toList());
    }

    public Memo saveMemo(String content, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Memo memo = new Memo(content, member);

        return memoRepository.save(memo);
    }

    public void toggleMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memo.toggleCheck();
    }

    public void deleteMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId).orElseThrow();
        memoRepository.delete(memo);
    }

    public Memo findById(Long memoId) {
        return memoRepository.findById(memoId).orElseThrow();
    }

    public void updateMemo(Long id, String content) {
        Memo memo = memoRepository.findById(id).orElseThrow();
        memo.updateContent(content);
    }

    public void updateWriterId(Long guestId, Long memberId) {
        List<Memo> guestMemoList = memoRepository.findMemoList(guestId);
        Member member = memberRepository.findById(memberId).orElseThrow();

        guestMemoList.forEach(memo -> memo.updateWriter(member));
    }

    public List<Memo> searchMemos(MemoSearchCond cond, Long writerId) {
        return memoRepository.searchMemos(cond, writerId);
    }
}
