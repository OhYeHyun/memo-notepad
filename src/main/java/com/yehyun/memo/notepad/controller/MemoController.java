package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.Memo;
import com.yehyun.memo.notepad.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
