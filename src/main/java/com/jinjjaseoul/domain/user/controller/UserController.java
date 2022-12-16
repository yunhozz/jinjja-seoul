package com.jinjjaseoul.domain.user.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.user.dto.query.ProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.UserCardQueryDto;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.model.UserRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/diligent")
    public Response getDiligentCurators() {
        List<UserCardQueryDto> userCardQueryDtoList = userRepository.findDiligentCurators();
        return Response.success(HttpStatus.OK, userCardQueryDtoList);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/me")
    public Response getMyInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProfileQueryDto profileQueryDto = userRepository.findProfileById(userPrincipal.getId());
        return Response.success(HttpStatus.OK, profileQueryDto);
    }

    @PostMapping("/join")
    public Response join(@Valid @RequestBody UserRequestDto userRequestDto) {
        Long userId = userService.join(userRequestDto);
        return Response.success(HttpStatus.CREATED, userId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/update")
    public Response updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody UpdateRequestDto updateRequestDto) {
        userService.updateProfile(userPrincipal.getId(), updateRequestDto);
        return Response.success(HttpStatus.CREATED, "프로필 업데이트가 완료되었습니다.");
    }
}