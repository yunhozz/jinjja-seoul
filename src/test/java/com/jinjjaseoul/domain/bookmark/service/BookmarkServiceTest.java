package com.jinjjaseoul.domain.bookmark.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.bookmark.dto.LocationCardResponseDto;
import com.jinjjaseoul.domain.bookmark.model.entity.CurationLikes;
import com.jinjjaseoul.domain.bookmark.model.entity.LocationBookmark;
import com.jinjjaseoul.domain.bookmark.model.repository.CurationLikesRepository;
import com.jinjjaseoul.domain.bookmark.model.repository.LocationBookmarkRepository;
import com.jinjjaseoul.domain.bookmark.service.exception.AlreadyBookmarkException;
import com.jinjjaseoul.domain.location.model.entity.Address;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @InjectMocks
    BookmarkService bookmarkService;

    @Mock
    MapRepository<CurationMap> curationMapRepository;

    @Mock
    CurationLikesRepository curationLikesRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    LocationBookmarkRepository locationBookmarkRepository;

    User user;
    Location location;

    @BeforeEach
    void beforeEach() {
        user = createUser("test@gmail.com", "tester");
        location = createLocation("test-location");
    }

    @Test
    @DisplayName("큐레이션 지도 좋아요")
    void makeLikesOfCurationMap() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long curationMapId = 100L;
        CurationMap curationMap = createCurationMap();
        CurationLikes curationLikes = new CurationLikes(user, curationMap);

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));
        given(curationLikesRepository.existsByUserAndCurationMap(any(User.class), any(CurationMap.class))).willReturn(false);
        given(curationLikesRepository.save(any(CurationLikes.class))).willReturn(curationLikes);

        // when
        Long result = bookmarkService.makeLikesOfCurationMap(userPrincipal, curationMapId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result).isEqualTo(curationLikes.getId());
        assertThat(curationLikes.getCurationMap()).isEqualTo(curationMap);
        assertThat(curationMap.getNumOfLikes()).isEqualTo(1);
    }

    @Test
    @DisplayName("큐레이션 지도 좋아요 시 중복")
    void makeLikesOfCurationMapThrowDuplicateException() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long curationMapId = 100L;
        CurationMap curationMap = createCurationMap();

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));
        given(curationLikesRepository.existsByUserAndCurationMap(any(User.class), any(CurationMap.class))).willReturn(true);

        // then
        assertThrows(AlreadyBookmarkException.class, () -> bookmarkService.makeLikesOfCurationMap(userPrincipal, curationMapId));
    }

    @Test
    @DisplayName("장소 북마크 생성")
    void makeBookmarkOfLocation() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long locationId = 100L;
        LocationBookmark locationBookmark = new LocationBookmark(user, location);

        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(locationBookmarkRepository.existsByUserAndLocation(any(User.class), any(Location.class))).willReturn(false);
        given(locationBookmarkRepository.save(any(LocationBookmark.class))).willReturn(locationBookmark);

        // when
        Long result = bookmarkService.makeBookmarkOfLocation(userPrincipal, locationId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result).isEqualTo(locationBookmark.getId());
        assertThat(locationBookmark.getLocation()).isEqualTo(location);
    }

    @Test
    @DisplayName("장소 북마크 생성 시 중복")
    void makeBookmarkOfLocationThrowDuplicateException() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long locationId = 100L;

        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(locationBookmarkRepository.existsByUserAndLocation(any(User.class), any(Location.class))).willReturn(true);

        // then
        assertThrows(AlreadyBookmarkException.class, () -> bookmarkService.makeBookmarkOfLocation(userPrincipal, locationId));
    }

    @Test
    @DisplayName("큐레이션 지도 좋아요 취소")
    void deleteCurationLikes() throws Exception {
        // given
        Long curationLikesId = 200L;
        CurationMap curationMap = createCurationMap();
        CurationLikes curationLikes = new CurationLikes(user, curationMap);

        given(curationLikesRepository.findWithCurationMapById(anyLong())).willReturn(Optional.of(curationLikes));
        willDoNothing().given(curationLikesRepository).delete(curationLikes);

        // when
        curationLikes.addLikesOfCurationMap(); // to test subtracting likes

        // then
        assertDoesNotThrow(() -> bookmarkService.deleteCurationLikes(curationLikesId));
        assertThat(curationMap.getNumOfLikes()).isEqualTo(0);
    }

    @Test
    @DisplayName("장소 북마크 취소")
    void deleteLocationBookmark() throws Exception {
        // given
        Long locationBookmarkId = 100L;
        LocationBookmark locationBookmark = new LocationBookmark(user, location);

        given(locationBookmarkRepository.findById(anyLong())).willReturn(Optional.of(locationBookmark));
        willDoNothing().given(locationBookmarkRepository).delete(locationBookmark);

        // then
        assertDoesNotThrow(() -> bookmarkService.deleteLocationBookmark(locationBookmarkId));
    }

    @Test
    @DisplayName("북마크한 장소 리스트 조회")
    void findLocationCardListByUserId() throws Exception {
        // given
        Long userId = 1L;
        List<Long> locationIds = List.of(100L, 200L, 300L);

        List<Location> locations = new ArrayList<>() {{
            Location location1 = createLocation("location1");
            Location location2 = createLocation("location2");
            Location location3 = createLocation("location3");

            add(location1);
            add(location2);
            add(location3);
        }};

        given(locationBookmarkRepository.findLocationIdsByUserId(anyLong())).willReturn(locationIds);
        given(locationRepository.findAllById(anyList())).willReturn(locations);

        // when
        List<LocationCardResponseDto> result = bookmarkService.findLocationCardListByUserId(userId);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("name").containsExactly("location1", "location2", "location3");
    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .role(Role.USER)
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

    private CurationMap createCurationMap() {
        return CurationMap.builder()
                .name("test-map")
                .user(user)
                .build();
    }
}