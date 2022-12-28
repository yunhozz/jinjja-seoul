package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.domain.location.dto.response.ImageResponseDto;
import com.jinjjaseoul.domain.location.model.entity.Image;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.ImageRepository;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.location.service.exception.AttachImageException;
import com.jinjjaseoul.domain.location.service.exception.ImageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final LocationRepository locationRepository;

    @Value("${file.directory}")
    private String fileDir;

    @Transactional
    public List<Long> uploadImages(List<MultipartFile> files, Long locationId) {
        if (files.isEmpty()) return null;
        List<Long> imageIds = new ArrayList<>();
        Location location = locationRepository.getReferenceById(locationId);

        try {
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String savedName = UUID.randomUUID() + extension;
                String savedPath = fileDir + savedName;

                Image image = createImage(location, originalName, savedName, savedPath);
                imageRepository.save(image);
                imageIds.add(image.getId());

                file.transferTo(new File(savedPath)); // 로컬에 업로드 처리
            }

        } catch (Exception e) {
            throw new AttachImageException();
        }

        return imageIds;
    }

    @Transactional(readOnly = true)
    public List<ImageResponseDto> getImageDtoList(Long locationId) {
        Location location = locationRepository.getReferenceById(locationId);
        List<Image> images = imageRepository.findByLocation(location);

        return new ArrayList<>() {{
            for (Image image : images) {
                ImageResponseDto imageResponseDto = new ImageResponseDto(image);
                add(imageResponseDto);
            }
        }};
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);
        byte[] imageArr;

        try {
            FileInputStream inputStream = new FileInputStream(image.getSavedPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readCount;

            while ((readCount = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readCount);
            }

            imageArr = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            throw new AttachImageException();
        }

        return imageArr;
    }

    private Image createImage(Location location, String originalName, String savedName, String savedPath) {
        return Image.builder()
                .location(location)
                .originalName(originalName)
                .savedName(savedName)
                .savedPath(savedPath)
                .build();
    }
}