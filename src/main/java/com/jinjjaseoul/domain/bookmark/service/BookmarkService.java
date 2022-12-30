package com.jinjjaseoul.domain.bookmark.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.domain.bookmark.dto.LocationCardResponseDto;
import com.jinjjaseoul.domain.bookmark.model.entity.CurationLikes;
import com.jinjjaseoul.domain.bookmark.model.entity.LocationBookmark;
import com.jinjjaseoul.domain.bookmark.model.repository.CurationLikesRepository;
import com.jinjjaseoul.domain.bookmark.model.repository.LocationBookmarkRepository;
import com.jinjjaseoul.domain.bookmark.service.exception.AlreadyBookmarkException;
import com.jinjjaseoul.domain.bookmark.service.exception.CurationLikesNotFoundException;
import com.jinjjaseoul.domain.bookmark.service.exception.LocationBookmarkNotFoundException;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final MapRepository<CurationMap> curationMapRepository;
    private final CurationLikesRepository curationLikesRepository;
    private final LocationRepository locationRepository;
    private final LocationBookmarkRepository locationBookmarkRepository;

    @Transactional
    public Long makeLikesOfCurationMap(UserPrincipal userPrincipal, Long curationMapId) {
        User user = userPrincipal.getUser();
        CurationMap curationMap = findCurationMap(curationMapId);
        validateAlreadyLikeOnCurationMap(user, curationMap); // 큐레이션 지도 좋아요 중복 검증

        CurationLikes curationLikes = new CurationLikes(user, curationMap);
        curationLikes.addLikesOfCurationMap(); // 큐레이션 지도 좋아요 +1

        return curationLikesRepository.save(curationLikes).getId();
    }

    @Transactional
    public Long makeBookmarkOfLocation(UserPrincipal userPrincipal, Long locationId) {
        User user = userPrincipal.getUser();
        Location location = locationRepository.getReferenceById(locationId);
        validateAlreadyBookmarkLocation(user, location); // 장소 북마크 중복 검증

        LocationBookmark locationBookmark = new LocationBookmark(user, location);
        return locationBookmarkRepository.save(locationBookmark).getId();
    }

    @Transactional
    public void deleteCurationLikes(Long curationLikesId) {
        CurationLikes curationLikes = curationLikesRepository.findWithCurationMapById(curationLikesId)
                .orElseThrow(CurationLikesNotFoundException::new);
        curationLikes.cancelLikesOfCurationMap(); // 큐레이션 지도 좋아요 -1
        curationLikesRepository.delete(curationLikes);
    }

    @Transactional
    public void deleteLocationBookmark(Long locationBookmarkId) {
        LocationBookmark locationBookmark = locationBookmarkRepository.findById(locationBookmarkId)
                .orElseThrow(LocationBookmarkNotFoundException::new);
        locationBookmarkRepository.delete(locationBookmark);
    }

    @Transactional(readOnly = true)
    public List<LocationCardResponseDto> findLocationCardListByUSerId(Long userId) {
        List<Long> locationIds = locationBookmarkRepository.findLocationIdsByUserId(userId);
        List<Location> locations = locationRepository.findAllById(locationIds);

        return new ArrayList<>() {{
            for (Location location : locations) {
                LocationCardResponseDto locationCardResponseDto = new LocationCardResponseDto(location);
                add(locationCardResponseDto);
            }
        }};
    }

    private CurationMap findCurationMap(Long curationMapId) {
        try {
            return curationMapRepository.findById(curationMapId)
                    .orElseThrow(CurationMapNotFoundException::new);

        } catch (ClassCastException e) {
            throw new CurationMapNotFoundException();
        }
    }

    private void validateAlreadyLikeOnCurationMap(User user, CurationMap curationMap) {
        if (curationLikesRepository.existsByUserAndCurationMap(user, curationMap)) {
            throw new AlreadyBookmarkException();
        }
    }

    private void validateAlreadyBookmarkLocation(User user, Location location) {
        if (locationBookmarkRepository.existsByUserAndLocation(user, location)) {
            throw new AlreadyBookmarkException();
        }
    }
}