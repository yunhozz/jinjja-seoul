package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.model.entity.CurationLocation;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.CurationLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNameDuplicateException;
import com.jinjjaseoul.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class CurationMapServiceTest {

    @InjectMocks
    CurationMapService curationMapService;

    @Mock
    IconRepository iconRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    MapRepository<CurationMap> curationMapRepository;

    @Mock
    CurationLocationRepository curationLocationRepository;

    User user;
    Icon icon;
    Location location;

    @BeforeEach
    void beforeEach() {
        user = createUser("test@gmail.com", "tester");
        icon = new Icon("test.ico", "test");
        location = createLocation("test-location");
    }

    @Test
    @DisplayName("큐레이션 지도 생성")
    void makeCurationMap() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        given(curationMapRepository.existsByName(anyString())).willReturn(false);
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);
        given(curationMapRepository.save(any(CurationMap.class))).willReturn(curationMap);

        // when
        Long result = curationMapService.makeCurationMap(userPrincipal, curationMapRequestDto);

        // then
        assertDoesNotThrow(() -> result);
        assertThat(curationMap).isNotNull();
        assertThat(curationMap.getId()).isEqualTo(result);
        assertThat(curationMap.getName()).isEqualTo("test-map");
        assertThat(curationMap.getIcon()).isEqualTo(icon);
    }

    @Test
    @DisplayName("큐레이션 지도 생성 시 중복 이름 검증 실패")
    void makeCurationMapThrowDuplicateException() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);

        given(curationMapRepository.existsByName(anyString())).willReturn(true);

        // then
        assertThrows(CurationMapNameDuplicateException.class, () -> curationMapService.makeCurationMap(userPrincipal, curationMapRequestDto));
    }

    @Test
    @DisplayName("큐레이션 장소 추가")
    void addCurationLocation() throws Exception {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(user);
        Long curationMapId = 10L;
        Long locationId = 100L;
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(locationId);

        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        CurationLocation curationLocation = CurationLocation.builder()
                .user(user)
                .curationMap(curationMap)
                .location(location)
                .build();

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));
        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(curationLocationRepository.save(any(CurationLocation.class))).willReturn(curationLocation);

        // then
        assertDoesNotThrow(() -> curationMapService.addCurationLocation(userPrincipal, curationMapId, locationSimpleRequestDto));
        assertThat(curationLocation.getUser()).isEqualTo(user);
        assertThat(curationLocation.getCurationMap()).isEqualTo(curationMap);
        assertThat(curationLocation.getLocation()).isEqualTo(location);
    }

    @Test
    @DisplayName("큐레이션 지도 정보 수정")
    void updateCurationMapInfo() throws Exception {
        // given
        Long curationMapId = 100L;
        Long iconId = 5L;
        CurationMapRequestDto curationMapUpdateRequestDto = new CurationMapRequestDto("update-name", iconId, false, false, false);
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        given(curationMapRepository.existsByName(anyString())).willReturn(false);
        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);

        // then
        assertDoesNotThrow(() -> curationMapService.updateCurationMapInfo(curationMapId, curationMapUpdateRequestDto));
        assertThat(curationMap.getName()).isEqualTo("update-name");
        assertThat(curationMap.isMakeTogether()).isFalse();
        assertThat(curationMap.isProfileDisplay()).isFalse();
        assertThat(curationMap.isShared()).isFalse();
    }

    @Test
    @DisplayName("큐레이션 지도 검색용 테이블 업데이트")
    void updateMapSearchTable() throws Exception {
        // given
        Long curationMapId = 100L;
        MapSearchRequestDto mapSearchRequestDto =
                new MapSearchRequestDto(Place.APGUJUNG, Somebody.ALONE, Something.BOOK, Characteristics.CASUAL, Food.DESERT, Beverage.BEER, Category.BAR);

        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));

        // then
        assertDoesNotThrow(() -> curationMapService.updateMapSearchTable(curationMapId, mapSearchRequestDto));
        assertThat(curationMap.getMapSearch().getPlace()).isEqualTo(Place.APGUJUNG);
        assertThat(curationMap.getMapSearch().getSomebody()).isEqualTo(Somebody.ALONE);
        assertThat(curationMap.getMapSearch().getSomething()).isEqualTo(Something.BOOK);
        assertThat(curationMap.getMapSearch().getCharacteristics()).isEqualTo(Characteristics.CASUAL);
        assertThat(curationMap.getMapSearch().getFood()).isEqualTo(Food.DESERT);
        assertThat(curationMap.getMapSearch().getBeverage()).isEqualTo(Beverage.BEER);
        assertThat(curationMap.getMapSearch().getCategory()).isEqualTo(Category.BAR);
    }

    @Test
    @DisplayName("큐레이션 지도 삭제")
    void deleteCurationMap() throws Exception {
        // given
        Long curationMapId = 100L;
        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        List<Long> curationLocationIds = List.of(10L, 11L, 12L, 13L, 14L);

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));
        given(curationLocationRepository.findIdsByCurationMapId(anyLong())).willReturn(curationLocationIds);
        willDoNothing().given(curationLocationRepository).deleteAllByIds(curationLocationIds);

        // then
        assertDoesNotThrow(() -> curationMapService.deleteCurationMap(curationMapId));
        assertThat(curationMap.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("큐레이션 지도 삭제 중복")
    void deleteCurationMapThrowDeleteException() throws Exception {
        // given
        Long curationMapId = 100L;
        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        given(curationMapRepository.findById(anyLong())).willReturn(Optional.of(curationMap));

        // when
        curationMap.delete(); // make already deleted

        // then
        assertThrows(IllegalStateException.class, () -> curationMapService.deleteCurationMap(curationMapId), "이미 삭제된 지도입니다.");
    }

    @Test
    @DisplayName("큐레이션 장소 삭제")
    void deleteCurationLocation() throws Exception {
        // given
        Long curationLocationId = 10L;
        Long iconId = 5L;
        CurationMapRequestDto curationMapRequestDto = new CurationMapRequestDto("test-map", iconId, true, true, true);
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        CurationLocation curationLocation = CurationLocation.builder()
                .user(user)
                .curationMap(curationMap)
                .location(location)
                .build();

        given(curationLocationRepository.findById(anyLong())).willReturn(Optional.of(curationLocation));
        willDoNothing().given(curationLocationRepository).delete(curationLocation);

        // then
        assertDoesNotThrow(() -> curationMapService.deleteCurationLocation(curationLocationId));
    }

    private CurationMap createCurationMap(CurationMapRequestDto curationMapRequestDto, User user, Icon icon) {
        return CurationMap.builder()
                .name(curationMapRequestDto.getName())
                .user(user)
                .icon(icon)
                .isMakeTogether(curationMapRequestDto.getIsMakeTogether())
                .isProfileDisplay(curationMapRequestDto.getIsProfileDisplay())
                .isShared(curationMapRequestDto.getIsShared())
                .build();
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
                .build();
    }
}