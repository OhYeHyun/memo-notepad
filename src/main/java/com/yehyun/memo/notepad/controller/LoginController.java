package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.login.form.LoginForm;
import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.service.dto.MemberSaveForm;
import com.yehyun.memo.notepad.service.LoginService;
import com.yehyun.memo.notepad.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final MemberService memberService;

    @GetMapping
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("error", error);
        return "login/login";
    }

    @GetMapping("/signup")
    public String saveForm(Model model) {
        model.addAttribute("memberSaveForm", new MemberSaveForm());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String save(@Valid @ModelAttribute MemberSaveForm form, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("오류 발생: {}", bindingResult);
            return "login/signup";
        }

        try {
            memberService.joinMember(form);
            return "login/login";

        } catch (IllegalArgumentException e) {
            bindingResult.reject("login", e.getMessage());
            return "login/signup";
        }
    }

    @GetMapping("/no")
    public String noLogin(@SessionAttribute(name = "guestId", required = false) String guestId,
                          HttpServletRequest request) {

        if (guestId == null) {
            guestId = UUID.randomUUID().toString();
            request.getSession().setAttribute("guestId", guestId);
            log.info("새로운 게스트 UUID 생성: {}", guestId);
        }

        return "redirect:/notepad/memos";
    }
}
