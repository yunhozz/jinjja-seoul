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
import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.LocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.CurationMapService;
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

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurationMapController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class CurationMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CurationMapService curationMapService;

    @MockBean
    MapRepository<CurationMap> curationMapRepository;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("[GET] /api/curations : ???????????? ?????? ????????? ??????")
    void getCurationMapListTest() throws Exception {
        //given
        CurationMapQueryDto curationMapQueryDto = new CurationMapQueryDto(1L, "test-map", "map.ico", "tester", "user.ico");
        given(curationMapRepository.findRandomList()).willReturn(List.of(curationMapQueryDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/curations")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_curation_map_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("???????????? ?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("result.data[].curationMapImageUrl").type(STRING).description("???????????? ?????? ????????? url"),
                                fieldWithPath("result.data[].userName").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].userImageUrl").type(STRING).description("?????? ????????? url"),
                                fieldWithPath("result.data[].locationNum").type(NUMBER).description("?????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("[GET] /api/curations/{id} : ???????????? ?????? ??????")
    void getLocationListTest() throws Exception {
        //given
        LocationSimpleQueryDto locationSimpleQueryDto = new LocationSimpleQueryDto(1L, "location", "si", "gu", "dong", "etc", 1L, "tester", "This is test", "user.ico");
        given(curationMapRepository.findLocationListByCurationMapId(anyLong())).willReturn(List.of(locationSimpleQueryDto));

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/curations/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_curation_location_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data[].locationName").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].si").type(STRING).description("???"),
                                fieldWithPath("result.data[].gu").type(STRING).description("???"),
                                fieldWithPath("result.data[].dong").type(STRING).description("???"),
                                fieldWithPath("result.data[].etc").type(STRING).description("????????? ??????"),
                                fieldWithPath("result.data[].userId").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data[].name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].introduction").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data[].imageUrl").type(STRING).description("?????? ????????? url"),
                                fieldWithPath("result.data[].largestComment").optional().type(STRING).description("?????? ??? ?????????")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/curations/create : ???????????? ?????? ??????")
    void createCurationMapTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", 1L, true, true, true);
        given(curationMapService.makeCurationMap(any(UserPrincipal.class), any(CurationMapRequestDto.class))).willReturn(1L);

        //when
        ResultActions result = mockMvc.perform(post("/api/curations/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(curationMapRequestDto))
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("create_curation_map",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("iconId").type(NUMBER).description("????????? id"),
                                fieldWithPath("isMakeTogether").type(BOOLEAN).description("?????? ?????? ??????"),
                                fieldWithPath("isProfileDisplay").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("isShared").type(BOOLEAN).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(NUMBER).description("???????????? ?????? id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/curations/{id}/update : ???????????? ?????? ??????")
    void recommendCurationLocationTest() throws Exception {
        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(1L);
        willDoNothing().given(curationMapService).addCurationLocation(any(UserPrincipal.class), anyLong(), any(LocationSimpleRequestDto.class));

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/curations/{id}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationSimpleRequestDto))
                .requestAttr("userPrincipal", userPrincipal)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_curation_location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
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
    @DisplayName("[PATCH] /api/curations/{id}/condition")
    void updateSearchConditionTest() throws Exception {
        //given
        MapSearchRequestDto mapSearchRequestDto = new MapSearchRequestDto(Place.APGUJUNG, Somebody.ALONE, Something.CAR, Characteristics.CASUAL, Food.DESERT, Beverage.BEER, Category.BAR);
        willDoNothing().given(curationMapService).updateMapSearchTable(anyLong(), any(MapSearchRequestDto.class));

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/curations/{id}/condition", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapSearchRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_curation_map_search_condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
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
    @DisplayName("[PATCH] /api/curations/{id}/update")
    void updateCurationMapTest() throws Exception {
        //given
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", 1L, true, true, true);
        willDoNothing().given(curationMapService).updateCurationMapInfo(anyLong(), any(CurationMapRequestDto.class));

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/curations/{id}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(curationMapRequestDto))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("update_curation_map_info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("iconId").type(NUMBER).description("????????? id"),
                                fieldWithPath("isMakeTogether").type(BOOLEAN).description("?????? ?????? ??????"),
                                fieldWithPath("isProfileDisplay").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("isShared").type(BOOLEAN).description("?????? ??????")
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
    @DisplayName("[PATCH] /api/curations/{id}/delete")
    void deleteCurationMapTest() throws Exception {
        //given
        willDoNothing().given(curationMapService).deleteCurationMap(anyLong());

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/curations/{id}/delete", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("delete_curation_map",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
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
    @DisplayName("[DELETE] /api/curations/location/{id}/delete")
    void deleteCurationLocationTest() throws Exception {
        //given
        willDoNothing().given(curationMapService).deleteCurationLocation(anyLong());

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/curations/location/{id}/delete", 1L)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("delete_curation_location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("???????????? ?????? id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data").type(STRING).description("?????? ?????????")
                        )
                ));
    }
}