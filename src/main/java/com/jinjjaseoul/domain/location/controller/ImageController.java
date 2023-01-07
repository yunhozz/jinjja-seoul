package com.jinjjaseoul.domain.location.controller;

import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.location.dto.response.ImageResponseDto;
import com.jinjjaseoul.domain.location.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 여러 이미지 조회 -> 이미지 id 를 포함한 dto 리스트 반환
    @GetMapping
    public Response viewImagesOnLocation(@RequestParam Long locationId) {
        List<ImageResponseDto> imageResponseDtoList = imageService.getImageDtoList(locationId);
        return Response.success(HttpStatus.OK, imageResponseDtoList);
    }

    // 단일 이미지 조회
    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> viewImageOnLocation(@PathVariable("id") Long imageId) {
        byte[] image = imageService.getImage(imageId);
        return ResponseEntity.ok(image);
    }

    // 이미지 업로드
    @PostMapping("/upload")
    public Response uploadImagesOnLocation(@RequestParam Long locationId, @RequestPart(value = "images", required = false) List<MultipartFile> files) {
        List<Long> imageIds = imageService.uploadImages(files, locationId);
        return Response.success(HttpStatus.CREATED, imageIds);
    }
}