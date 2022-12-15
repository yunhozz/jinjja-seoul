package com.jinjjaseoul.domain.user.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.ProfileResponseDto;
import com.jinjjaseoul.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/me")
    public Response getMyInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProfileResponseDto profileResponseDto = userService.findProfileDto(userPrincipal);
        return Response.success(HttpStatus.OK, profileResponseDto);
    }

    @PostMapping("/join")
    public Response join(@Valid @RequestBody UserRequestDto userRequestDto) {
        Long userId = userService.join(userRequestDto);
        return Response.success(HttpStatus.CREATED, userId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/update")
    public Response updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateRequestDto updateRequestDto) {
        userService.updateProfile(userPrincipal, updateRequestDto);
        return Response.success(HttpStatus.CREATED, "프로필 업데이트가 완료되었습니다.");
    }
}