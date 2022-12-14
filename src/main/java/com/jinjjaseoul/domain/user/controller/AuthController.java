package com.jinjjaseoul.domain.user.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.domain.user.dto.request.LoginRequestDto;
import com.jinjjaseoul.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Response login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, response);
        return Response.success(HttpStatus.CREATED, tokenResponseDto);
    }

    @Secured("{ROLE_ADMIN, ROLE_USER}")
    @PostMapping("/issue")
    public Response tokenReIssue(@RequestHeader("Authorization") String refreshToken, @AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletResponse response) {
        TokenResponseDto tokenResponseDto = authService.reissue(refreshToken, userPrincipal, response);
        return Response.success(HttpStatus.CREATED, tokenResponseDto);
    }

    @Secured("{ROLE_ADMIN, ROLE_USER}")
    @PostMapping("/logout")
    public Response logout(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        authService.logout(accessToken, userPrincipal);
        return Response.success(HttpStatus.CREATED, "로그아웃이 완료되었습니다.");
    }

    @Secured("{ROLE_ADMIN, ROLE_USER}")
    @PatchMapping("/withdraw")
    public Response withdraw(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        authService.withdraw(accessToken, userPrincipal);
        return Response.success(HttpStatus.CREATED, "회원 탈퇴가 완료되었습니다.");
    }
}