package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.Memo;
import com.yehyun.memo.notepad.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/memos")
public class MemoController {

    private final MemoRepository memoRepository;

    @GetMapping
    public String listMemos(Model model) {
        model.addAttribute("memos", memoRepository.getAll());
        return "memo";
    }

    @PostMapping
    public String createMemo(@RequestParam("content") String content) {
        memoRepository.save(new Memo(content));
        log.info("저장됨 {}", content);
        return "redirect:/memos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleCheck(@PathVariable("id") Long id) {
        Memo memo = memoRepository.findById(id);
        memo.setCheck(!memo.isCheck());

        return "redirect:/memos";
    }

    @PostMapping("/{id}/delete")
    public String deleteMemo(@PathVariable("id") Long id) {
        Memo memo = memoRepository.findById(id);
        memoRepository.delete(memo);
        return "redirect:/memos";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("수정 {}", id);

        Memo memo = memoRepository.findById(id);
        List<Memo> memoList = memoRepository.getAll();

        model.addAttribute("memoToEdit", memo);
        model.addAttribute("memos", memoList);
        return "editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @RequestParam String content) {
        Memo memo = memoRepository.findById(id);
        memo.setContent(content);
        return "redirect:/memos";
    }
}
