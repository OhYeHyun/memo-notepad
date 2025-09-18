package com.notepad.user.controller;

import com.notepad.auth.dto.JwtPrincipal;
import com.notepad.auth.jwt.JwtLoginSuccessProcessor;
import com.notepad.user.service.GuestService;
import com.notepad.dto.request.user.UserSaveRequest;
import com.notepad.user.service.UserService;
import com.notepad.validator.ValidationException;
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

    private final UserService userService;
    private final GuestService guestLoginService;
    private final JwtLoginSuccessProcessor jwtLoginSuccessProcessor;

    @GetMapping
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login/login";
    }

    @GetMapping("/signup")
    public String saveForm(Model model) {
        model.addAttribute("memberSaveForm", new UserSaveRequest());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String save(@Valid @ModelAttribute UserSaveRequest form,
                       BindingResult bindingResult,
                       @RequestParam(defaultValue = "/notepad/memos") String redirectURL,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "login/signup";
        }

        try {
            JwtPrincipal jwtPrincipal = userService.createUser(form);
            jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, jwtPrincipal);

            return "redirect:" + redirectURL;
        } catch (ValidationException e) {
            bindingResult.rejectValue(e.getField(), e.getErrorCode());

            return "login/signup";
        }
    }

    @GetMapping("/no")
    public String noLogin(HttpServletRequest request, HttpServletResponse response) {
        JwtPrincipal jwtPrincipal = guestLoginService.createGuest();
        jwtLoginSuccessProcessor.reissueTokensAndAuthenticate(request, response, jwtPrincipal);

        return "redirect:/notepad/memos";
    }
}
