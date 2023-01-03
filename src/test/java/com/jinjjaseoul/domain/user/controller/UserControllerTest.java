package com.jinjjaseoul.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.query.ProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.UserCardQueryDto;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.UserService;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    IconRepository iconRepository;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("[GET] /api/users : 열혈 큐레이터 리스트 조회")
    void getDiligentCuratorsTest() throws Exception {
        //given
        Icon icon = new Icon("test-icon.ico", anyString());
        given(iconRepository.getReferenceById(1L)).willReturn(icon);

        UserCardQueryDto userCardQueryDto = new UserCardQueryDto(1L, "tester", 0, icon.getImageUrl());
        given(userRepository.findDiligentCurators()).willReturn(List.of(userCardQueryDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_diligent_users",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("유저 id"),
                                fieldWithPath("result.data[].name").type(STRING).description("유저 이름"),
                                fieldWithPath("result.data[].numOfRecommend").type(NUMBER).description("테마 지도 개수"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("아이콘 url")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/users/me : 자신의 정보 조회")
    void getMyInfoTest() throws Exception {
        //given
        User user = User.builder()
                .email("test@gmail.com")
                .name("tester")
                .role(Role.USER)
                .build();
        given(userService.join(any())).willReturn(user.getId());

        Icon icon = new Icon("test.ico", anyString());
        given(iconRepository.getReferenceById(1L)).willReturn(icon);

        ProfileQueryDto profileQueryDto = new ProfileQueryDto(1L, "tester", "This is test", icon.getImageUrl());
        given(userRepository.findProfileById(user.getId())).willReturn(profileQueryDto);

        //when
        ResultActions result = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_my_info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data.id").type(NUMBER).description("유저 id"),
                                fieldWithPath("result.data.name").type(STRING).description("유저 이름"),
                                fieldWithPath("result.data.introduction").type(STRING).description("유저 소개"),
                                fieldWithPath("result.data.imageUrl").type(STRING).description("아이콘 url")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.ADMIN)
    @DisplayName("[GET] /api/users/{id} : 특정 유저 상세 정보 조회")
    void getUserInfoTest() throws Exception {
        //given
        UserResponseDto userResponseDto = new UserResponseDto(1L, "test@gmail.com", "123", "tester", "This is test", Role.USER, Provider.LOCAL, LocalDateTime.now(), LocalDateTime.now());
        given(userService.findUserDtoById(1L)).willReturn(userResponseDto);

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_user_info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data.id").type(NUMBER).description("유저 id"),
                                fieldWithPath("result.data.email").type(STRING).description("유저 이메일"),
                                fieldWithPath("result.data.password").type(STRING).description("유저 비밀번호"),
                                fieldWithPath("result.data.name").type(STRING).description("유저 이름"),
                                fieldWithPath("result.data.introduction").type(STRING).description("유저 소개"),
                                fieldWithPath("result.data.role").type(STRING).description("유저 권한"),
                                fieldWithPath("result.data.provider").type(STRING).description("OAuth2 제공자"),
                                fieldWithPath("result.data.createdDate").type(STRING).description("생성 일자"),
                                fieldWithPath("result.data.lastModifiedDate").type(STRING).description("수정 일자")
                        )
                ));
    }

    @Test
    @DisplayName("[POST] /api/users/join : 회원가입")
    void joinTest() throws Exception {
        //given
        UserRequestDto userRequestDto = new UserRequestDto("test@gmail.com", "123", "tester");
        given(userService.join(userRequestDto)).willReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post("/api/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("join_request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("name").type(STRING).description("이름")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(NUMBER).description("유저 id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[PATCH] /api/users/update : 프로필 업데이트")
    void updateProfileTest() throws Exception {
        //given
        UpdateRequestDto updateRequestDto = new UpdateRequestDto("tester", anyString(), anyLong());

        //when
        ResultActions result = mockMvc.perform(patch("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("이름"),
                                fieldWithPath("introduction").type(STRING).description("자기소개"),
                                fieldWithPath("iconId").type(NUMBER).description("아이콘 id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(STRING).description("결과 메세지")
                        )
                ));
    }
}