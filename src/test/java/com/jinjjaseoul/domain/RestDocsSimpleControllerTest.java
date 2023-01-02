package com.jinjjaseoul.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class RestDocsSimpleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/health-check")
    public void healthCheckTest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/health-check")
                .accept(APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("healthcheck", responseFields(
                                fieldWithPath("success").type(BOOLEAN).description("성공 유무"),
                                fieldWithPath("status").type(STRING).description("상태 코드"),
                                fieldWithPath("result.data").type(STRING).description("반환 메세지")
                        ))
                );
    }
}