package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSaveForm;
import com.yehyun.memo.notepad.domain.memo.form.MemoUpdateForm;
import com.yehyun.memo.notepad.repository.MemoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/notepad/memos")
public class MemoController {

    private final MemoRepository memoRepository;

    @GetMapping()
    public String listMemos(Model model) {
        model.addAttribute("memoSaveForm", new MemoSaveForm());
        model.addAttribute("memos", memoRepository.getAll());
        return "memo";
    }

    @PostMapping
    public String createMemo(@Valid @ModelAttribute MemoSaveForm memo, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            model.addAttribute("memos", memoRepository.getAll());
            return "/memo";
        }

        memoRepository.save(new Memo(memo.getContent()));
        log.info("저장됨 {}", memo.getContent());
        return "redirect:/notepad/memos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleCheck(@PathVariable("id") Long id) {
        Memo memo = memoRepository.findById(id);
        memo.setCheck(!memo.isCheck());

        return "redirect:/notepad/memos";
    }

    @PostMapping("/{id}/delete")
    public String deleteMemo(@PathVariable("id") Long id) {
        Memo memo = memoRepository.findById(id);
        memoRepository.delete(memo);
        return "redirect:/notepad/memos";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("수정 {}", id);

        Memo memo = memoRepository.findById(id);
        List<Memo> memoList = memoRepository.getAll();

        model.addAttribute("memoUpdateForm", memo);
        model.addAttribute("memos", memoList);
        return "editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute MemoUpdateForm memo, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoRepository.getAll());
            return "editMemo";
        }

        Memo savedMemo = memoRepository.findById(id);
        savedMemo.setContent(memo.getContent());
        return "redirect:/notepad/memos";
    }
}
