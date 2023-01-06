package com.jinjjaseoul.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.bookmark.dto.LocationCardResponseDto;
import com.jinjjaseoul.domain.bookmark.service.BookmarkService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationBookmarkController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class LocationBookmarkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookmarkService bookmarkService;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/location/bookmarks : 북마크한 장소 리스트 조회")
    void getLocationBookmarkListByUserTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        LocationCardResponseDto locationCardResponseDto = new LocationCardResponseDto(1L, "test-location", "si", "gu", "dong", "etc", "100", "200");
        given(bookmarkService.findLocationCardListByUserId(userPrincipal.getId())).willReturn(List.of(locationCardResponseDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/location/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_location_bookmark_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data[].locationId").type(NUMBER).description("장소 id"),
                                fieldWithPath("result.data[].name").type(STRING).description("장소 이름"),
                                fieldWithPath("result.data[].si").type(STRING).description("시"),
                                fieldWithPath("result.data[].gu").type(STRING).description("구"),
                                fieldWithPath("result.data[].dong").type(STRING).description("동"),
                                fieldWithPath("result.data[].etc").type(STRING).description("나머지 주소"),
                                fieldWithPath("result.data[].nx").type(STRING).description("x 좌표"),
                                fieldWithPath("result.data[].ny").type(STRING).description("y 좌표")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/location/bookmarks : 장소 북마크 생성")
    void makeBookmarkOfLocationTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        given(bookmarkService.makeBookmarkOfLocation(eq(userPrincipal), anyLong())).willReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post("/api/location/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .param("locationId", String.valueOf(1L))
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("create_location_bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("locationId").description("장소 id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(NUMBER).description("장소 북마크 id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[DELETE] /api/location/bookmarks/{id}/cancel : 장소 북마크 취소")
    void cancelBookmarkOfLocationTest() throws Exception {
        //given
        willDoNothing().given(bookmarkService).deleteLocationBookmark(anyLong());

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/location/bookmarks/{id}/cancel", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("cancel_location_bookmark",
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