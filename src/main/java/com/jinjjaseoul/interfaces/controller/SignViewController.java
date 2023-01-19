package com.jinjjaseoul.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class SignViewController {

    @GetMapping("/login-page")
    public String loginPage(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        // 로그인 페이지 직전 페이지를 세션에 저장
        if (referer != null && !referer.contains("/login-page")) {
            HttpSession session = request.getSession();
            session.setAttribute("prevPage", referer);
        }

        return "login-page";
    }

    @GetMapping("/sign-up")
    public String joinForm() {
        return "join";
    }

    @GetMapping("/sign-in")
    public String loginForm() {
        return "login";
    }
}