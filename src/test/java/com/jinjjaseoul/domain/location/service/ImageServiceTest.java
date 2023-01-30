package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.domain.location.dto.response.ImageResponseDto;
import com.jinjjaseoul.domain.location.model.entity.Address;
import com.jinjjaseoul.domain.location.model.entity.Image;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.ImageRepository;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Mock
    ImageRepository imageRepository;

    @Mock
    LocationRepository locationRepository;

    Location location;

    @BeforeEach
    void beforeEach() {
        location = createLocation("test-location");
    }

    @Test
    @DisplayName("이미지 업로드")
    void uploadImages() throws Exception {
        // given
        Long locationId = 100L;
        List<MultipartFile> files = new ArrayList<>() {{
            MultipartFile multipartFile1 = new MockMultipartFile("file1", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpeg data>>".getBytes());
            MultipartFile multipartFile2 = new MockMultipartFile("file2", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpeg data>>".getBytes());
            MultipartFile multipartFile3 = new MockMultipartFile("file3", "test3.jpg", MediaType.IMAGE_JPEG_VALUE, "<<jpeg data>>".getBytes());

            add(multipartFile1);
            add(multipartFile2);
            add(multipartFile3);
        }};

        Image image = createImage("test");

        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(imageRepository.save(any(Image.class))).willReturn(image);

        // when
        List<Long> result = imageService.uploadImages(files, locationId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("이미지 dto 리스트 조회")
    void getImageDtoList() throws Exception {
        // given
        Long locationId = 100L;
        Image image1 = createImage("test1");
        Image image2 = createImage("test2");
        Image image3 = createImage("test3");

        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(imageRepository.findByLocation(any(Location.class))).willReturn(List.of(image1, image2, image3));

        // when
        List<ImageResponseDto> result = imageService.getImageDtoList(locationId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("savedName").contains("[save]test1");
    }

    private Image createImage(String originalName) {
        return Image.builder()
                .location(location)
                .originalName(originalName)
                .savedName("[save]" + originalName)
                .savedPath("path")
                .build();
    }

    private Location createLocation(String name) {
        return Location.builder()
                .name(name)
                .address(new Address("si", "gu", "dong", "etc"))
                .nx("100")
                .ny("200")
                .build();
    }
}