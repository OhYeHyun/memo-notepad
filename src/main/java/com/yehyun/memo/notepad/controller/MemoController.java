package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSaveForm;
import com.yehyun.memo.notepad.domain.memo.form.MemoUpdateForm;
import com.yehyun.memo.notepad.service.MemoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/notepad/memos")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public String listMemos(Model model) {
        model.addAttribute("memoSaveForm", new MemoSaveForm());
        model.addAttribute("memos", memoService.getAllMemos());
        return "memo/memo";
    }

    @PostMapping
    public String createMemo(@Valid @ModelAttribute MemoSaveForm form, BindingResult bindingResult,
                             @SessionAttribute(name = "loginMemberId", required = false) Long loginMemberId,
                             @SessionAttribute(name = "guestId", required = false) String guestId,
                             HttpServletRequest request, Model model) {

        if (loginMemberId == null && guestId == null) {
            guestId = UUID.randomUUID().toString();
            request.getSession().setAttribute("guestId", guestId);
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoService.getAllMemos());
            return "memo/memo";
        }

        Memo memo = memoService.saveMemo(form.getContent(), loginMemberId, guestId);
        log.info("저장됨 {}", memo.getContent());

        return "redirect:/notepad/memos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleCheck(@PathVariable("id") Long id) {
        memoService.toggleMemo(id);
        return "redirect:/notepad/memos";
    }

    @PostMapping("/{id}/delete")
    public String deleteMemo(@PathVariable("id") Long id) {
        memoService.deleteMemo(id);
        return "redirect:/notepad/memos";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("수정 {}", id);

        model.addAttribute("memoUpdateForm", memoService.findById(id));
        model.addAttribute("memos", memoService.getAllMemos());
        return "memo/editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@Valid @ModelAttribute MemoUpdateForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoService.getAllMemos());
            return "memo/editMemo";
        }

        memoService.updateMemo(form.getId(), form.getContent(), form.isCheck());
        return "redirect:/notepad/memos";
    }
}
