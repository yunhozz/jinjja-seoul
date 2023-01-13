package com.jinjjaseoul.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/map-finder")
@RequiredArgsConstructor
public class SearchViewController {

    @GetMapping
    public String redirectPageWithKeyword(@RequestParam String keyword, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("keyword", keyword);
        return "search-redirect";
    }

    @PostMapping
    public String searchPage(HttpServletRequest request, Model model) {
        String keyword = request.getParameter("keyword");
        model.addAttribute("keyword", keyword);
        return "map-list";
    }
}