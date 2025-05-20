package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.login.form.LoginForm;
import com.yehyun.memo.notepad.service.GuestLoginService;
import com.yehyun.memo.notepad.service.dto.MemberSaveForm;
import com.yehyun.memo.notepad.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final MemberService memberService;
    private final GuestLoginService guestLoginService;

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
    public String noLogin(HttpServletResponse response) {
        String token = guestLoginService.createGuestToken();

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setMaxAge(216);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return "redirect:/notepad/memos";
    }
}
