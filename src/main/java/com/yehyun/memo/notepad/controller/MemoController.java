package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSaveForm;
import com.yehyun.memo.notepad.domain.memo.form.MemoUpdateForm;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/notepad/memos")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public String listMemos(Model model, @AuthenticationPrincipal PrincipalMember principalMember) {

        model.addAttribute("memoSaveForm", new MemoSaveForm());
        model.addAttribute("memos", memoService.getAllMemos(principalMember.getName()));
        model.addAttribute("principalMemberName", principalMember.getName());

        log.info("memoSaveForm: {}", model.getAttribute("memoSaveForm"));

        return "memo/memo";
    }

    @PostMapping
    public String createMemo(@Valid @ModelAttribute MemoSaveForm form, BindingResult bindingResult,
                             @AuthenticationPrincipal PrincipalMember principalMember,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoService.getAllMemos(principalMember.getName()));
            return "memo/memo";
        }

        Memo memo = memoService.saveMemo(form.getContent(), principalMember.getName());
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
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal PrincipalMember principalMember) {

        log.info("수정 {}", id);

        model.addAttribute("memoUpdateForm", memoService.findById(id));
        model.addAttribute("memos", memoService.getAllMemos(principalMember.getName()));
        model.addAttribute("principalMemberName", principalMember.getName());

        return "memo/editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@Valid @ModelAttribute MemoUpdateForm form,
                       BindingResult bindingResult, Model model,
                       @AuthenticationPrincipal PrincipalMember principalMember) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoService.getAllMemos(principalMember.getName()));
            return "memo/editMemo";
        }

        memoService.updateMemo(form.getId(), form.getContent());
        return "redirect:/notepad/memos";
    }
}
