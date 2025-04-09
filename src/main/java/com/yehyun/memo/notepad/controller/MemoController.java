package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.domain.memo.Memo;
import com.yehyun.memo.notepad.domain.memo.form.MemoSaveForm;
import com.yehyun.memo.notepad.domain.memo.form.MemoUpdateForm;
import com.yehyun.memo.notepad.repository.MemoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/notepad/memos")
public class MemoController {

    private final MemoRepository memoRepository;

    @GetMapping
    public String listMemos(Model model) {
        model.addAttribute("memoSaveForm", new MemoSaveForm());
        model.addAttribute("memos", memoRepository.getAll());
        return "memo/memo";
    }

    @PostMapping
    public String createMemo(@Valid @ModelAttribute MemoSaveForm form, BindingResult bindingResult,
                             @SessionAttribute(name = "loginMember", required = false) Long loginMemberId,
                             @SessionAttribute(name = "guestId", required = false) String guestId,
                             HttpServletRequest request, Model model) {

        if (loginMemberId == null && guestId == null) {
            guestId = UUID.randomUUID().toString();
            request.getSession().setAttribute("guestId", guestId);
            return "redirect:/notepad/memos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoRepository.getAll());
            return "memo/memo";
        }

        String writerId = (loginMemberId != null) ? String.valueOf(loginMemberId) : guestId;

        Memo memo = new Memo(form.getContent(), writerId);
        memoRepository.save(memo);
        log.info("저장됨 {}", form.getContent());
        return "redirect:/notepad/memos";
    }

    @PostMapping("/{id}/toggle")
    public String toggleCheck(@PathVariable("id") Long id) {
        Memo memo = memoRepository.findById(id);
        memo.toggleCheck();
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
        return "memo/editMemo";
    }

    @PostMapping("/{id}/edit")
    public String edit(@Valid @ModelAttribute MemoUpdateForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memos", memoRepository.getAll());
            return "memo/editMemo";
        }

        Memo savedMemo = memoRepository.findById(form.getId());
        savedMemo.update(form.getContent());
        savedMemo.setCheck(form.isCheck());
        return "redirect:/notepad/memos";
    }
}
