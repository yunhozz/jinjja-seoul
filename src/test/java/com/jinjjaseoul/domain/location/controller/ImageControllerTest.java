package com.jinjjaseoul.domain.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjjaseoul.annotation.WithAuthUser;
import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.location.dto.response.ImageResponseDto;
import com.jinjjaseoul.domain.location.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ImageService imageService;

    @MockBean
    JwtService jwtService;

    @MockBean
    RedisUtils redisUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/images : 장소에 대한 이미지 리스트 조회")
    void viewImagesOnLocationTest() throws Exception {
        //given
        ImageResponseDto imageResponseDto = new ImageResponseDto(1L, "test-image", "C:/test.jpg", LocalDateTime.now());
        given(imageService.getImageDtoList(anyLong())).willReturn(List.of(imageResponseDto));

        //when
        ResultActions result = mockMvc.perform(get("/api/images")
                .contentType(MediaType.APPLICATION_JSON)
                .param("locationId", String.valueOf(1L))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_image_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("locationId").description("장소 id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data[].id").type(NUMBER).description("이미지 id"),
                                fieldWithPath("result.data[].savedName").type(STRING).description("저장된 이미지 이름"),
                                fieldWithPath("result.data[].savedPath").type(STRING).description("저장된 이미지 경로"),
                                fieldWithPath("result.data[].createdDate").type(STRING).description("생성 일자")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[GET] /api/images/{id} : 장소에 대한 이미지 단건 조회")
    void viewImageOnLocationTest() throws Exception {
        //given
        given(imageService.getImage(anyLong())).willReturn(any());

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/images/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("get_image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("이미지 id")
                        )
                ));
    }

    @Test
    @WithAuthUser(role = Role.USER)
    @DisplayName("[POST] /api/images/upload : 이미지 업로드")
    void uploadImagesOnLocationTest() throws Exception {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("images", "test.jpeg", MediaType.IMAGE_JPEG_VALUE, "<<jpeg data>>".getBytes());
        given(imageService.uploadImages(anyList(), any())).willReturn(List.of(1L));

        //when
        ResultActions result = mockMvc.perform(multipart("/api/images/upload")
                .file(multipartFile)
                .param("locationId", String.valueOf(1L))
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("upload_image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("images").description("첨부 이미지 리스트")
                        ),
                        requestParameters(
                                parameterWithName("locationId").description("장소 id")
                        ),
                        responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(ARRAY).description("이미지 id 리스트")
                        )
                ));
    }
}