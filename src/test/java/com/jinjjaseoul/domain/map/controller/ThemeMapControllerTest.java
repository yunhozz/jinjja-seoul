package com.jinjjaseoul.domain.map.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.ThemeMapService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThemeMapController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class ThemeMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ThemeMapService themeMapService;

    @MockBean
    MapRepository<ThemeMap> themeMapRepository;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("[GET] /api/themes/recommend : ?????? ?????? ?????? ????????? ??????")
    void getRecommendListTest() throws Exception {
        //given
        ThemeMapQueryDto themeMapQueryDto = new ThemeMapQueryDto(1L, "test-map", "test.ico");
        given(themeMapRepository.findRecommendList()).willReturn(List.of(themeMapQueryDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/themes/recommend")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_theme_map_recommend_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("????????? url"),
                                fieldWithPath("result.data[].curatorNum").type(NUMBER).description("???????????? ???")
                        )
                ));
    }

    @Test
    @DisplayName("[GET] /api/themes/latest : ?????? ?????? ?????? ????????? ??????")
    void getLatestListTest() throws Exception {
        //given
        ThemeMapQueryDto themeMapQueryDto = new ThemeMapQueryDto(1L, "test-map", "test.ico");
        given(themeMapRepository.findLatestList()).willReturn(List.of(themeMapQueryDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/themes/latest")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_theme_map_latest_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("????????? url"),
                                fieldWithPath("result.data[].curatorNum").type(NUMBER).description("???????????? ???")
                        )
                ));
    }

    @Test
    @DisplayName("[GET] /api/themes/popular : ?????? ?????? ?????? ????????? ??????")
    void getPopularListTest() throws Exception {
        //given
        ThemeMapQueryDto themeMapQueryDto = new ThemeMapQueryDto(1L, "test-map", "test.ico");
        given(themeMapRepository.findPopularList()).willReturn(List.of(themeMapQueryDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/themes/popular")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_theme_map_popular_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("????????? url"),
                                fieldWithPath("result.data[].curatorNum").type(NUMBER).description("???????????? ???")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/themes/{id} : ?????? ?????? ????????? ?????? ????????? ??????")
    void getLocationListTest() throws Exception {
        //given
        ThemeLocationSimpleQueryDto themeLocationSimpleQueryDto = new ThemeLocationSimpleQueryDto(1L, 1L, "test-location", "user.ico");
        given(themeMapRepository.findLocationListByThemeMapId(anyLong())).willReturn(List.of(themeLocationSimpleQueryDto));

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/themes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_theme_location_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("result.data[].locationId").type(NUMBER).description("?????? ?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("?????? ????????? url")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/themes/create : ?????? ?????? ??????")
    void createThemeMapTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(1L);
        given(themeMapService.makeThemeMap(userPrincipal.getId(), locationSimpleRequestDto)).willReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post("/api/themes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationSimpleRequestDto))
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("create_theme_map",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("locationId").type(NUMBER).description("?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(NUMBER).description("?????? ?????? id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/themes/update : ?????? ?????? ??????")
    void recommendThemeLocationTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(1L);
        willDoNothing().given(themeMapService).updateThemeLocation(userPrincipal.getId(), 1L, locationSimpleRequestDto);

        //when
        ResultActions result = mockMvc.perform(post("/api/themes/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationSimpleRequestDto))
                .param("id", String.valueOf(1L))
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_theme_location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("id").description("?????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("locationId").type(NUMBER).description("?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(NUMBER).description("?????? id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.ADMIN)
    @DisplayName("[PATCH] /api/themes/{id}/condition : ?????? ?????? ?????? ?????? ????????????")
    void updateSearchConditionTest() throws Exception {
        //given
        MapSearchRequestDto mapSearchRequestDto = new MapSearchRequestDto(Place.APGUJUNG, Somebody.ALONE, Something.CAR, Characteristics.CASUAL, Food.DESERT, Beverage.BEER, Category.BAR);
        willDoNothing().given(themeMapService).updateMapSearchTable(1L, mapSearchRequestDto);

        //when
        ResultActions result = mockMvc.perform(patch("/api/themes/{id}/condition", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapSearchRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_theme_map_search_condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("place").type(STRING).description("?????????"),
                                fieldWithPath("somebody").type(STRING).description("?????????"),
                                fieldWithPath("something").type(STRING).description("?????????"),
                                fieldWithPath("characteristics").type(STRING).description("?????????/??????"),
                                fieldWithPath("food").type(STRING).description("??????"),
                                fieldWithPath("beverage").type(STRING).description("???/??????"),
                                fieldWithPath("category").type(STRING).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[PATCH] /api/themes/{id}/delete : ?????? ?????? ??????")
    void deleteThemeMapTest() throws Exception {
        //given
        willDoNothing().given(themeMapService).deleteThemeMap(anyLong());

        //when
        ResultActions result = mockMvc.perform(patch("/api/themes/{id}/delete", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("delete_theme_map",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(STRING).description("?????? ?????????")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[DELETE] /api/themes/location/{id}/delete : ?????? ????????? ?????? ?????? ?????? ??????")
    void deleteThemeLocationTest() throws Exception {
        //given
        willDoNothing().given(themeMapService).deleteThemeLocation(anyLong());

        //when
        ResultActions result = mockMvc.perform(delete("/api/themes/location/{id}/delete", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("delete_theme_location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("?????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(STRING).description("?????? ?????????")
                        )
                ));
    }
}