package com.jinjjaseoul.domain.user.controller;

import com.jinjjaseoul.auth.jwt.TokenResponseDto;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.user.dto.LoginRequestDto;
import com.jinjjaseoul.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Response login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, request, response);
        return Response.success(HttpStatus.CREATED, tokenResponseDto);
    }

    @GetMapping("/token")
    public Response tokenReissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.tokenReissue(request, response);
        return Response.success(HttpStatus.OK, tokenResponseDto);
    }

    @GetMapping("/logout")
    public Response logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return Response.success(HttpStatus.OK);
    }

    @PatchMapping("/withdraw")
    public Response withdraw(@AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletRequest request, HttpServletResponse response) {
        authService.withdraw(userPrincipal.getId(), request, response);
        return Response.success(HttpStatus.CREATED);
    }
}