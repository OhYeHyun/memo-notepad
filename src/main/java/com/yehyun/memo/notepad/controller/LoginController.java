package com.yehyun.memo.notepad.controller;

import com.yehyun.memo.notepad.domain.member.Member;
import com.yehyun.memo.notepad.security.dto.PrincipalMember;
import com.yehyun.memo.notepad.security.jwt.JwtLoginSuccessProcessor;
import com.yehyun.memo.notepad.security.jwt.JwtRequestParser;
import com.yehyun.memo.notepad.security.service.PrincipalFactory;
import com.yehyun.memo.notepad.service.GuestLoginService;
import com.yehyun.memo.notepad.service.dto.MemberSaveForm;
import com.yehyun.memo.notepad.service.MemberService;
import com.yehyun.memo.notepad.validator.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;
    private final JwtRequestParser jwtRequestParser;
    private final PrincipalFactory principalFactory;

    @GetMapping
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login/login";
    }

    @GetMapping("/signup")
    public String saveForm(Model model) {
        model.addAttribute("memberSaveForm", new MemberSaveForm());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String save(@Valid @ModelAttribute MemberSaveForm form,
                       BindingResult bindingResult,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       @RequestParam(defaultValue = "/notepad/memos") String redirectURL) {

        if (bindingResult.hasErrors()) {
            return "login/signup";
        }

        try {
            String guestToken = jwtRequestParser.extractTokenFromCookies(request.getCookies());
            if (guestToken != null) {
                Member guest = jwtRequestParser.createMemberFromToken(guestToken);
                Member member = memberService.joinMember(form, guest);
                jwtLoginSuccessProcessor.processSuccess(response, principalFactory.create(member));
            } else {
                Member member = memberService.joinMember(form);
                jwtLoginSuccessProcessor.processSuccess(response, principalFactory.create(member));
            }
            return "redirect:" + redirectURL;

        } catch (ValidationException e) {
            bindingResult.rejectValue(e.getField(), e.getErrorCode());

            return "login/signup";
        }
    }

    @GetMapping("/no")
    public String noLogin(HttpServletResponse response) {
        PrincipalMember guestMember = guestLoginService.createGuestPrincipal();
        jwtLoginSuccessProcessor.processSuccess(response, guestMember);

        return "redirect:/notepad/memos";
    }
}
