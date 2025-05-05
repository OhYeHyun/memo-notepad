package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.login.form.LoginForm;
import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.domain.member.form.MemberSaveForm;
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

    private final LoginService loginService;
    private final MemberService memberService;

    @GetMapping
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login/login";
    }

    @PostMapping
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/notepad/memos") String redirectURL, HttpServletRequest request) {

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("login", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/login";
        }

        if (bindingResult.hasErrors()) {
            log.info("오류 발생: {}", bindingResult);
            return "login/login";
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMemberId", loginMember.getId());
        log.info("로그인 세션 생성: {}", loginMember.getId());

        return "redirect:" + redirectURL;
    }

    @GetMapping("/signup")
    public String saveForm(Model model) {
        model.addAttribute("memberSaveForm", new MemberSaveForm());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String save(@Valid @ModelAttribute MemberSaveForm form, BindingResult bindingResult,
                       @RequestParam(defaultValue = "/notepad/memos") String redirectURL, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            log.info("오류 발생: {}", bindingResult);
            return "login/signup";
        }

        try {
            Member member = memberService.joinMember(form.getName(), form.getLoginId(), form.getPassword());

            HttpSession session = request.getSession();
            session.setAttribute("loginMemberId", member.getId());
            log.info("로그인 세션 생성: {}", member.getId());
            return "redirect:" + redirectURL;

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
