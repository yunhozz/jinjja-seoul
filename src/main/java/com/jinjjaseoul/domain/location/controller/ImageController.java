package com.jinjjaseoul.domain.location.controller;

import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.location.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/download")
    public Response downloadImagesOnLocation(@RequestParam Long locationId) throws MalformedURLException {
        List<Resource> resources = imageService.downloadImages(locationId);
        return Response.success(HttpStatus.OK, resources);
    }

    @PostMapping("/upload")
    public Response uploadImagesOnLocation(@RequestParam Long locationId, @RequestParam("images") List<MultipartFile> files) throws IOException {
        List<Long> imageIds = imageService.uploadImages(files, locationId);
        return Response.success(HttpStatus.CREATED, imageIds);
    }
}