package com.jinjjaseoul.interfaces.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/map-finder")
@RequiredArgsConstructor
public class SearchViewController {

    @GetMapping
    public String redirectPageWithKeyword(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        return "search-redirect";
    }

    @PostMapping
    public String searchPage(HttpServletRequest request, Model model) {
        String keyword = request.getParameter("keyword");
        model.addAttribute("keyword", keyword);
        return "map-list";
    }
}