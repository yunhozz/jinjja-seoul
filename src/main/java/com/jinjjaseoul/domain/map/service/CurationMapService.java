package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.common.converter.MapConverter;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.model.entity.CurationLocation;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.CurationLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.curation_map.CurationMapRepository;
import com.jinjjaseoul.domain.map.service.exception.CurationLocationNotFoundException;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CurationMapService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;
    private final LocationRepository locationRepository;
    private final CurationMapRepository curationMapRepository;
    private final CurationLocationRepository curationLocationRepository;

    @Transactional
    public Long makeCurationMap(Long userId, Long iconId, CurationMapRequestDto curationMapRequestDto) {
        User user = userRepository.getReferenceById(userId);
        Icon icon = determineIcon(iconId);
        CurationMap curationMap = MapConverter.convertToCurationMapEntity(curationMapRequestDto, user, icon);

        return curationMapRepository.save(curationMap).getId();
    }

    // 하나의 큐레이션맵 당 여러 장소 추천 가능, 공개 여부에 따라 다른 유저도 추천 가능
    @Transactional
    public void addCurationLocation(Long userId, Long curationMapId, Long locationId, String imageUrl) {
        User user = userRepository.getReferenceById(userId);
        CurationMap curationMap = findCurationMap(curationMapId);
        Location location = locationRepository.getReferenceById(locationId);

        CurationLocation curationLocation = createCurationLocation(user, curationMap, location, imageUrl);
        curationLocationRepository.save(curationLocation);
    }

    @Transactional
    public void deleteCurationMap(Long curationMapId) {
        CurationMap curationMap = findCurationMap(curationMapId);
        curationMapRepository.delete(curationMap);
    }

    @Transactional
    public void deleteCurationLocation(Long curationLocationId) {
        CurationLocation curationLocation = curationLocationRepository.findById(curationLocationId)
                .orElseThrow(CurationLocationNotFoundException::new);
        curationLocationRepository.delete(curationLocation);
    }

    private CurationLocation createCurationLocation(User user, CurationMap curationMap, Location location, String imageUrl) {
        return CurationLocation.builder()
                .user(user)
                .curationMap(curationMap)
                .location(location)
                .imageUrl(imageUrl)
                .build();
    }

    private CurationMap findCurationMap(Long curationMapId) {
        return curationMapRepository.findById(curationMapId)
                .orElseThrow(CurationMapNotFoundException::new);
    }

    private Icon determineIcon(Long iconId) {
        Icon icon;
        if (iconId != null) {
            icon = iconRepository.getReferenceById(iconId);

        } else {
            Random random = new Random(iconRepository.count());
            icon = iconRepository.getReferenceById(random.nextLong());
        }

        return icon;
    }
}