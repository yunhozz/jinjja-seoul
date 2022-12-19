package com.jinjjaseoul.domain.bookmark.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.domain.bookmark.model.entity.CurationLikes;
import com.jinjjaseoul.domain.bookmark.model.entity.LocationBookmark;
import com.jinjjaseoul.domain.bookmark.model.repository.CurationLikesRepository;
import com.jinjjaseoul.domain.bookmark.model.repository.LocationBookmarkRepository;
import com.jinjjaseoul.domain.bookmark.service.exception.CurationLikesNotFoundException;
import com.jinjjaseoul.domain.bookmark.service.exception.LocationBookmarkNotFoundException;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.curation_map.CurationMapRepository;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final UserRepository userRepository;
    private final CurationMapRepository curationMapRepository;
    private final CurationLikesRepository curationLikesRepository;
    private final LocationRepository locationRepository;
    private final LocationBookmarkRepository locationBookmarkRepository;

    @Transactional
    public Long makeLikesOfCurationMap(UserPrincipal userPrincipal, Long curationMapId) {
        User user = userPrincipal.getUser();
        CurationMap curationMap = curationMapRepository.findById(curationMapId)
                .orElseThrow(CurationMapNotFoundException::new);

        CurationLikes curationLikes = new CurationLikes(user, curationMap);
        curationMap.addLikes(); // 큐레이션 지도 좋아요 +1

        return curationLikesRepository.save(curationLikes).getId();
    }

    @Transactional
    public Long makeBookmarkOfLocation(UserPrincipal userPrincipal, Long locationId) {
        User user = userPrincipal.getUser();
        Location location = locationRepository.getReferenceById(locationId);
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
}