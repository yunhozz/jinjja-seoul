package com.jinjjaseoul.common;

import com.jinjjaseoul.common.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-check")
@RequiredArgsConstructor
public class RestDocsSimpleController {

    @GetMapping
    public Response healthcheck() {
        return Response.success(HttpStatus.OK, "service is health");
    }
}