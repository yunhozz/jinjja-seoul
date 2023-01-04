package com.jinjjaseoul.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.user.dto.request.LoginRequestDto;
import com.jinjjaseoul.domain.user.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    BCryptPasswordEncoder encoder;

    @Test
    @DisplayName("[POST] /api/auth/login : 유저 로그인")
    void loginTest() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");
        TokenResponseDto tokenResponseDto = new TokenResponseDto("type", "atk", "rtk", 10L);
        given(authService.login(any(), any())).willReturn(tokenResponseDto);

        //when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("login_request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("password").type(STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data.grantType").type(STRING).description("JWT 토큰 인증 타입"),
                                fieldWithPath("result.data.accessToken").type(STRING).description("JWT access token"),
                                fieldWithPath("result.data.refreshToken").type(STRING).description("JWT refresh token"),
                                fieldWithPath("result.data.refreshTokenValidTime").type(NUMBER).description("JWT refresh token 유효 시간")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/auth/issue : JWT 토큰 재발급")
    void tokenReissueTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        TokenResponseDto tokenResponseDto = new TokenResponseDto("type", "atk", "rtk", 10L);
        given(authService.reissue(anyString(), any(), any())).willReturn(tokenResponseDto);

        //when
        ResultActions result = mockMvc.perform(post("/api/auth/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponseDto.getRefreshToken())
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("token_reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data.grantType").type(STRING).description("JWT 토큰 인증 타입"),
                                fieldWithPath("result.data.accessToken").type(STRING).description("JWT access token"),
                                fieldWithPath("result.data.refreshToken").type(STRING).description("JWT refresh token"),
                                fieldWithPath("result.data.refreshTokenValidTime").type(NUMBER).description("JWT refresh token 유효 시간")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/auth/logout : 유저 로그아웃")
    void logoutTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        TokenResponseDto tokenResponseDto = new TokenResponseDto("type", "atk", "rtk", 10L);
        willDoNothing().given(authService).logout(tokenResponseDto.getAccessToken(), userPrincipal);

        //when
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenResponseDto.getAccessToken())
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("logout_request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(STRING).description("결과 메세지")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[PATCH] /api/auth/withdraw : 회원 탈퇴")
    void withdrawTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        willDoNothing().given(authService).withdraw(anyLong());

        //when
        ResultActions result = mockMvc.perform(patch("/api/auth/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("withdraw_request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(STRING).description("결과 메세지")
                        )
                ));
    }
}