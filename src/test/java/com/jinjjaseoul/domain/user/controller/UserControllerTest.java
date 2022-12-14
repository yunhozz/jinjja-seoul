package com.jinjjaseoul.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.user.dto.query.ProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.UserCardQueryDto;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("[GET] /api/users : ?????? ???????????? ????????? ??????")
    void getDiligentCuratorsTest() throws Exception {
        //given
        UserCardQueryDto userCardQueryDto = new UserCardQueryDto(1L, "tester", 0, "user.ico");
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
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].numOfRecommend").type(NUMBER).description("?????? ?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("????????? url")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/users/me : ????????? ?????? ??????")
    void getMyInfoTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        ProfileQueryDto profileQueryDto = new ProfileQueryDto(1L, "tester", "This is test", "user.ico");
        given(userRepository.findProfileById(userPrincipal.getId())).willReturn(profileQueryDto);

        //when
        ResultActions result = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_my_info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.id").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data.name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.introduction").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.imageUrl").type(STRING).description("????????? url")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.ADMIN)
    @DisplayName("[GET] /api/users/{id} : ?????? ?????? ?????? ?????? ??????")
    void getUserInfoTest() throws Exception {
        //given
        UserResponseDto userResponseDto = new UserResponseDto(1L, "test@gmail.com", "123", "tester", "This is test", Role.USER, Provider.LOCAL, LocalDateTime.now(), LocalDateTime.now());
        given(userService.findUserDtoById(anyLong())).willReturn(userResponseDto);

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_user_info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.id").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data.email").type(STRING).description("?????? ?????????"),
                                fieldWithPath("result.data.password").type(STRING).description("?????? ????????????"),
                                fieldWithPath("result.data.name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.introduction").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.role").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.provider").type(STRING).description("OAuth2 ?????????"),
                                fieldWithPath("result.data.createdDate").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.lastModifiedDate").type(STRING).description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("[POST] /api/users/join : ????????????")
    void joinTest() throws Exception {
        //given
        UserRequestDto userRequestDto = new UserRequestDto("test@gmail.com", "123", "tester");
        given(userService.join(userRequestDto)).willReturn(anyLong());

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
                                fieldWithPath("email").type(STRING).description("?????????"),
                                fieldWithPath("password").type(STRING).description("????????????"),
                                fieldWithPath("name").type(STRING).description("??????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(NUMBER).description("?????? id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[PATCH] /api/users/update : ????????? ????????????")
    void updateProfileTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        UpdateRequestDto updateRequestDto = new UpdateRequestDto("tester", "This is test", 1L);
        willDoNothing().given(userService).updateProfile(userPrincipal.getId(), updateRequestDto);

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
                                fieldWithPath("name").type(STRING).description("??????"),
                                fieldWithPath("introduction").type(STRING).description("????????????"),
                                fieldWithPath("iconId").type(NUMBER).description("????????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(STRING).description("?????? ?????????")
                        )
                ));
    }
}