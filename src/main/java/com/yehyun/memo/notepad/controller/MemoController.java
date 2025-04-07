package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.Memo;
import com.yehyun.memo.notepad.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemoController {

    private final MemoRepository memoRepository;

    @GetMapping("/")
    public String memoList(Model model) {
        model.addAttribute("memos", memoRepository.getAll());
        return "memo";
    }

    @PostMapping("/memo/save")
    public String saveMemo(@RequestParam("content") String content) {
        memoRepository.save(new Memo(content));
        log.info("저장됨 {}", content);
        return "redirect:/";
    }

    @PostMapping("/memo/action")
    public String deleteMemo(@RequestParam("id") Long id, @RequestParam String action) {
        Memo memo = memoRepository.findById(id);

        if (action.equals("check")) {
            memo.setCheck(!memo.isCheck());
        }
        if (action.equals("delete")) {
            memoRepository.delete(memo);
        }
        return "redirect:/";
    }

    @GetMapping("/memo/{memoId}/edit")
    public String editMemo(@PathVariable Long memoId, Model model) {
        log.info("수정 {}", memoId);

        Memo memo = memoRepository.findById(memoId);
        List<Memo> memoList = memoRepository.getAll();

        model.addAttribute("memoToEdit", memo);
        model.addAttribute("memos", memoList);
        return "editMemo";
    }

    @PostMapping("/memo/{memoId}/edit")
    public String edit(@PathVariable Long memoId, @RequestParam String content) {
        Memo memo = memoRepository.findById(memoId);
        memo.setContent(content);
        return "redirect:/";
    }
}
