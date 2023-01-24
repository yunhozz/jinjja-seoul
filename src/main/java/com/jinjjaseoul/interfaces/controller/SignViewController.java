package com.jinjjaseoul.interfaces.controller;

import com.jinjjaseoul.common.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class SignViewController {

    @GetMapping("/sign-in")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("Referer");
        // 로그인 페이지 직전 페이지를 쿠키에 저장
        if (referer != null && !referer.contains("/login-page")) {
            CookieUtils.addCookie(response, "prevPage", referer, 3600);
        }

        return "login";
    }

    @GetMapping("/login")
    public String redirectToLogin(@RequestParam String token, @RequestParam String target, RedirectAttributes attr) {
        attr.addFlashAttribute("token", token);
        attr.addFlashAttribute("target", target);
        return "redirect:/auth";
    }

    @GetMapping("/auth")
    public String saveTokenPage() {
        return "login-redirect";
    }
}