package com.jinjjaseoul.domain.map.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.map.dto.query.WholeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchMapController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class SearchMapControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MapRepository<?> mapRepository;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("[POST] /api/maps/search : ?????? ??????")
    void searchMapListTest() throws Exception {
        //given
        SearchRequestDto searchRequestDto = new SearchRequestDto("keyword", Place.APGUJUNG, Somebody.ALONE, Something.CAR, Characteristics.CASUAL, Food.DESERT, Beverage.BEER, List.of(Category.BAR));
        WholeMapQueryDto wholeMapQueryDto = new WholeMapQueryDto(1L, "test-map", "map.ico", "TM");
        PageImpl<WholeMapQueryDto> wholeMapQueryDtoPage = new PageImpl<>(List.of(wholeMapQueryDto), PageRequest.of(0, 30), 1);

        given(mapRepository.searchWholeMapListByKeyword(any(SearchRequestDto.class), anyLong(), any(Pageable.class))).willReturn(wholeMapQueryDtoPage);

        //when
        ResultActions result = mockMvc.perform(post("/api/maps/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequestDto))
                .param("mapKind", "??????")
                .param("lastMapId", "100")
                .param("page", "0")
                .param("size", "30")
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("search_whole_map",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("mapKind").optional().description("?????? ?????? (??????, ????????????, ??????????????????)"),
                                parameterWithName("lastMapId").optional().description("????????? ?????? id (?????? ?????????)"),
                                parameterWithName("page").description("?????? ?????????"),
                                parameterWithName("size").description("??? ???????????? ?????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("keyword").optional().type(STRING).description("?????? ?????????"),
                                fieldWithPath("place").optional().type(STRING).description("?????????"),
                                fieldWithPath("somebody").optional().type(STRING).description("?????????"),
                                fieldWithPath("something").optional().type(STRING).description("?????????"),
                                fieldWithPath("characteristics").optional().type(STRING).description("?????????/??????"),
                                fieldWithPath("food").optional().type(STRING).description("??????"),
                                fieldWithPath("beverage").optional().type(STRING).description("???/??????"),
                                fieldWithPath("categories").optional().type(ARRAY).description("???????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("status").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.content[].id").type(NUMBER).description("?????? id"),
                                fieldWithPath("result.data.content[].name").type(STRING).description("?????? ??????"),
                                fieldWithPath("result.data.content[].mapIconImgUrl").type(STRING).description("?????? ????????? url"),
                                fieldWithPath("result.data.content[].curatorNum").optional().type(NUMBER).description("???????????? ??? (?????? ?????? ??????)"),
                                fieldWithPath("result.data.content[].userInfo[].id").optional().type(NUMBER).description("?????? id (???????????? ?????? ??????)"),
                                fieldWithPath("result.data.content[].userInfo[].userName").optional().type(NUMBER).description("?????? ?????? (???????????? ?????? ??????)"),
                                fieldWithPath("result.data.content[].userInfo[].userIconImgUrl").optional().type(NUMBER).description("?????? ????????? url (???????????? ?????? ??????)"),
                                fieldWithPath("result.data.content[].locationNum").optional().type(NUMBER).description("?????? ?????? (???????????? ?????? ??????)"),
                                fieldWithPath("result.data.pageable.sort.empty").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("result.data.pageable.sort.sorted").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("result.data.pageable.sort.unsorted").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("result.data.pageable.offset").type(NUMBER).description("?????????"),
                                fieldWithPath("result.data.pageable.pageNumber").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("result.data.pageable.pageSize").type(NUMBER).description("????????? ??????"),
                                fieldWithPath("result.data.pageable.paged").type(BOOLEAN).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("result.data.pageable.unpaged").type(BOOLEAN).description("????????? ?????? ?????? ??????"),
                                fieldWithPath("result.data.last").type(BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("result.data.totalElements").type(NUMBER).description("??? ????????? ??????"),
                                fieldWithPath("result.data.totalPages").type(NUMBER).description("??? ????????? ??????"),
                                fieldWithPath("result.data.size").type(NUMBER).description("??? ???????????? ????????? ??????"),
                                fieldWithPath("result.data.number").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("result.data.sort.empty").type(BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("result.data.sort.sorted").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("result.data.sort.unsorted").type(BOOLEAN).description("?????? ??????"),
                                fieldWithPath("result.data.first").type(BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("result.data.numberOfElements").type(NUMBER).description("???????????? ????????? ????????? ??????"),
                                fieldWithPath("result.data.empty").type(BOOLEAN).description("???????????? ???????????? ??????")
                        )
                ));
    }
}