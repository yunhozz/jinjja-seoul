package com.jinjjaseoul.interfaces.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/map-finder")
@RequiredArgsConstructor
public class SearchViewController {

    @PostMapping
    public String redirectToSearchPage(@RequestParam(required = false) String keyword, RedirectAttributes attr) {
        attr.addFlashAttribute("keyword", keyword);
        return "redirect:/map-finder";
    }

    @GetMapping
    public String searchPage() {
        return "map-list";
    }
}