package com.notepad.core.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/app", "/app/**"})
    public String app() {
        return "forward:/app/index.html";
    }
}