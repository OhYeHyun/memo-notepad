package com.notepad.memo.controller;

import com.notepad.entity.Memo;
import com.notepad.dto.request.memo.MemoSaveRequest;
import com.notepad.dto.request.memo.MemoSearchRequest;
import com.notepad.dto.request.memo.MemoUpdateRequest;
import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.global.enums.Role;
import com.notepad.memo.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final MemoService memoService;

    @GetMapping
    public String listMemos(@ModelAttribute("memoSearch") MemoSearchRequest memoSearch,
                            Model model,
                            @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {

        List<Memo> allMemos = memoService.getAllMemos(memoSearch, jwtPrincipal.getId());

        model.addAttribute("memoSaveForm", new MemoSaveRequest());
        model.addAttribute("memos", allMemos);

        model.addAttribute("principalMemberName", jwtPrincipal.getName());
        model.addAttribute("principalMemberRole", jwtPrincipal.getRole());
        model.addAttribute("Role", Role.class);

        log.info("memoSaveForm: {}", model.getAttribute("memoSaveForm"));

        return "memo/memo";
    }

    @PostMapping
    public String createMemo(@Valid @ModelAttribute MemoSaveRequest form, BindingResult bindingResult,
                             @AuthenticationPrincipal JwtPrincipal jwtPrincipal,
                             Model model) {

        if (bindingResult.hasErrors()) {
            List<Memo> allMemos = memoService.getAllMemos(null, jwtPrincipal.getId());

            model.addAttribute("memos", allMemos);
            model.addAttribute("principalMemberName", jwtPrincipal.getName());
            model.addAttribute("principalMemberRole", jwtPrincipal.getRole());
            model.addAttribute("Role", Role.class);

            return "memo/memo";
        }

        Memo memo = memoService.saveMemo(form.getContent(), jwtPrincipal.getId());
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
                           @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {

        log.info("수정 {}", id);
        List<Memo> allMemos = memoService.getAllMemos(null, jwtPrincipal.getId());

        model.addAttribute("memoUpdateForm", memoService.findById(id));
        model.addAttribute("memos", allMemos);
        model.addAttribute("principalMemberName", jwtPrincipal.getName());

        return "memo/editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@Valid @ModelAttribute MemoUpdateRequest form,
                       BindingResult bindingResult, Model model,
                       @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {

        if (bindingResult.hasErrors()) {
            List<Memo> allMemos = memoService.getAllMemos(null, jwtPrincipal.getId());

            model.addAttribute("memos", allMemos);
            model.addAttribute("principalMemberName", jwtPrincipal.getName());

            return "memo/editMemo";
        }

        memoService.updateMemo(form.getId(), form.getContent());
        return "redirect:/notepad/memos";
    }
}
