package com.jinjjaseoul.domain.location.service;

import com.jinjjaseoul.domain.location.model.entity.Image;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.ImageRepository;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    public List<Long> uploadImages(List<MultipartFile> files, Long locationId) throws IOException {
        if (files.isEmpty()) {
            return null;
        }

        List<Long> imageIds = new ArrayList<>();
        Location location = locationRepository.getReferenceById(locationId);

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID() + extension;
            String savedPath = fileDir + savedName;

            Image image = createImage(location, originalName, savedName, savedPath);
            imageRepository.save(image);
            file.transferTo(new File(savedPath)); // 로컬에 업로드 처리

            imageIds.add(image.getId());
        }

        return imageIds;
    }

    @Transactional(readOnly = true)
    public List<Resource> downloadImages(Long locationId) throws MalformedURLException {
        Location location = locationRepository.getReferenceById(locationId);
        List<Image> images = imageRepository.findByLocation(location);

        return new ArrayList<>() {{
            for (Image image : images) {
                UrlResource urlResource = new UrlResource(image.getSavedPath());
                add(urlResource);
            }
        }};
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