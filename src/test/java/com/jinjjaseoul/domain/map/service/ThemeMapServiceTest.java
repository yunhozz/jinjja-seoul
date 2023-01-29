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
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapSimpleRequestDto;
import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.map.model.repository.ThemeLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNameDuplicateException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class ThemeMapServiceTest {

    @InjectMocks
    ThemeMapService themeMapService;

    @Mock
    UserRepository userRepository;

    @Mock
    IconRepository iconRepository;

    @Mock
    LocationRepository locationRepository;

    @Mock
    MapRepository<ThemeMap> themeMapRepository;

    @Mock
    ThemeLocationRepository themeLocationRepository;

    @Mock
    RedisUtils redisUtils;

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
    @DisplayName("테마 지도 생성 정보 임시 저장")
    void saveThemeMapInfoOnCache() throws Exception {
        // given
        Long iconId = 100L;
        UserPrincipal userPrincipal = new UserPrincipal(user);
        ThemeMapSimpleRequestDto themeMapSimpleRequestDto = new ThemeMapSimpleRequestDto("test", iconId, List.of(Category.CAFE, Category.BAR), "keyword");

        given(themeMapRepository.existsByName(anyString())).willReturn(false);
        willDoNothing().given(redisUtils).setCollection(anyString(), anyString(), anyLong(), anyList(), anyString());

        // then
        assertDoesNotThrow(() -> themeMapService.saveThemeMapInfoOnCache(userPrincipal, themeMapSimpleRequestDto));
    }

    @Test
    @DisplayName("테마 지도 생성 정보 입력 시 중복 이름 검증 실패")
    void saveThemeMapInfoOnCacheThrowNameException() throws Exception {
        // given
        Long iconId = 100L;
        UserPrincipal userPrincipal = new UserPrincipal(user);
        ThemeMapSimpleRequestDto themeMapSimpleRequestDto = new ThemeMapSimpleRequestDto("test", iconId, List.of(Category.CAFE, Category.BAR), "keyword");

        given(themeMapRepository.existsByName(anyString())).willReturn(true);

        // then
        assertThrows(ThemeMapNameDuplicateException.class, () -> themeMapService.saveThemeMapInfoOnCache(userPrincipal, themeMapSimpleRequestDto));
    }

    @Test
    @DisplayName("테마 지도 생성")
    void makeThemeMap() throws Exception {
        // given
        Long userId = 1L;
        Long iconId = 100L;
        Long locationId = 200L;

        List<Object> dataList = List.of("test", iconId, List.of(Category.CAFE, Category.BAR), "keyword");
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(locationId);
        ThemeMap themeMap = createThemeMap();

        given(redisUtils.getDataListFromCollection(anyString(), anyLong(), anyLong())).willReturn(dataList);
        given(userRepository.getReferenceById(anyLong())).willReturn(user);
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);
        given(locationRepository.getReferenceById(anyLong())).willReturn(location);
        given(themeMapRepository.save(any(ThemeMap.class))).willReturn(themeMap);

        willDoNothing().given(redisUtils).deleteData(anyString());

        // when
        Long result = themeMapService.makeThemeMap(userId, locationSimpleRequestDto);

        // then
        assertThat(themeMap).isNotNull();
        assertThat(themeMap.getId()).isEqualTo(result);
        assertThat(themeMap.getUser().getId()).isEqualTo(user.getId());
        assertThat(themeMap.getIcon().getId()).isEqualTo(icon.getId());
        assertThat(themeMap.getCategories()).containsExactly(Category.CAFE, Category.BAR);
        assertThat(user.getNumOfRecommend()).isEqualTo(1);
    }

    @Test
    @DisplayName("테마 지도 생성 시 장소 등록을 안했을 때")
    void makeThemeMapReturnNull() throws Exception {
        // given
        Long userId = 1L;
        Long iconId = 100L;
        Long locationId = null;

        List<Object> dataList = List.of("test", iconId, List.of(Category.CAFE, Category.BAR), "keyword");
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(locationId);
        given(redisUtils.getDataListFromCollection(anyString(), anyLong(), anyLong())).willReturn(dataList);

        // when
        Long result = themeMapService.makeThemeMap(userId, locationSimpleRequestDto);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("자신이 만든 테마 지도에서 테마 장소 업데이트")
    void updateThemeLocationByMe() throws Exception {
        // given
        Long userId = 1L;
        Long themeMapId = 100L;
        Long updateLocationId = 200L;
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(updateLocationId);

        ThemeMap themeMap = createThemeMap();
        ThemeLocation themeLocation = createThemeLocation(user, themeMap, location);
        Location updateLocation = createLocation("new-location");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(themeMapRepository.findById(anyLong())).willReturn(Optional.of(themeMap));
        given(locationRepository.getReferenceById(anyLong())).willReturn(updateLocation);
        given(themeLocationRepository.findByUserAndThemeMap(any(User.class), any(ThemeMap.class))).willReturn(Optional.of(themeLocation));

        // then
        assertDoesNotThrow(() -> themeMapService.updateThemeLocation(userId, themeMapId, locationSimpleRequestDto));
        assertThat(themeLocation.getUser()).isEqualTo(user);
        assertThat(themeLocation.getLocation()).isEqualTo(updateLocation);
    }

    @Test
    @DisplayName("타인이 만든 테마 지도에서 테마 장소 업데이트")
    void updateThemeLocationByOthers() throws Exception {
        // given
        Long anotherId = 2L;
        Long themeMapId = 100L;
        Long updateLocationId = 200L;
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(updateLocationId);

        User another = createUser("another@gmail.com", "another");
        ThemeMap themeMap = createThemeMap();
        ThemeLocation themeLocation = createThemeLocation(another, themeMap, location);
        Location updateLocation = createLocation("new-location");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(another));
        given(themeMapRepository.findById(anyLong())).willReturn(Optional.of(themeMap));
        given(locationRepository.getReferenceById(anyLong())).willReturn(updateLocation);
        given(themeLocationRepository.findByUserAndThemeMap(another, themeMap)).willReturn(Optional.of(themeLocation));

        // then
        assertDoesNotThrow(() -> themeMapService.updateThemeLocation(anotherId, themeMapId, locationSimpleRequestDto));
        assertThat(themeLocation.getUser()).isEqualTo(another);
        assertThat(themeLocation.getLocation()).isEqualTo(updateLocation);
    }

    @Test
    @DisplayName("타인이 만든 테마 지도에서 테마 장소 추가")
    void addThemeLocationByOthers() throws Exception {
        // given
        Long anotherId = 2L;
        Long themeMapId = 100L;
        Long updateLocationId = 200L;
        LocationSimpleRequestDto locationSimpleRequestDto = new LocationSimpleRequestDto(updateLocationId);

        User another = createUser("another@gmail.com", "another");
        ThemeMap themeMap = createThemeMap();
        Location updateLocation = createLocation("new-location");
        ThemeLocation themeLocation = createThemeLocation(another, themeMap, updateLocation);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(another));
        given(themeMapRepository.findById(anyLong())).willReturn(Optional.of(themeMap));
        given(locationRepository.getReferenceById(anyLong())).willReturn(updateLocation);
        given(themeLocationRepository.findByUserAndThemeMap(another, themeMap)).willReturn(Optional.empty());

        // then
        assertDoesNotThrow(() -> themeMapService.updateThemeLocation(anotherId, themeMapId, locationSimpleRequestDto));
        assertThat(themeLocation.getUser()).isEqualTo(another);
        assertThat(themeLocation.getLocation()).isEqualTo(updateLocation);
        assertThat(another.getNumOfRecommend()).isEqualTo(1);
    }

    @Test
    @DisplayName("검색용 테이블 업데이트")
    void updateMapSearchTable() throws Exception {
        // given
        Long themeMapId = 10L;
        ThemeMap themeMap = createThemeMap();
        MapSearchRequestDto mapSearchRequestDto =
                new MapSearchRequestDto(Place.APGUJUNG, Somebody.ALONE, Something.BOOK, Characteristics.CASUAL, Food.DESERT, Beverage.BEER, Category.BAR);

        given(themeMapRepository.findById(anyLong())).willReturn(Optional.of(themeMap));

        // then
        assertDoesNotThrow(() -> themeMapService.updateMapSearchTable(themeMapId, mapSearchRequestDto));
        assertThat(themeMap.getMapSearch().getPlace()).isEqualTo(Place.APGUJUNG);
        assertThat(themeMap.getMapSearch().getSomebody()).isEqualTo(Somebody.ALONE);
        assertThat(themeMap.getMapSearch().getSomething()).isEqualTo(Something.BOOK);
        assertThat(themeMap.getMapSearch().getCharacteristics()).isEqualTo(Characteristics.CASUAL);
        assertThat(themeMap.getMapSearch().getFood()).isEqualTo(Food.DESERT);
        assertThat(themeMap.getMapSearch().getBeverage()).isEqualTo(Beverage.BEER);
    }

    @Test
    @DisplayName("테마 지도 삭제")
    void deleteThemeMap() throws Exception {
        // given
        Long themeMapId = 10L;
        List<Long> userIds = List.of(1L, 2L, 3L);
        List<Long> themeLocationIds = List.of(100L, 200L, 300L, 400L ,500L);
        ThemeMap themeMap = createThemeMap();

        given(themeMapRepository.findById(anyLong())).willReturn(Optional.of(themeMap));
        given(themeLocationRepository.findIdsByThemeMapId(anyLong())).willReturn(themeLocationIds);
        given(userRepository.findIdsByThemeLocationIds(anyList())).willReturn(userIds);

        willDoNothing().given(themeLocationRepository).deleteAllByIds(themeLocationIds);
        willDoNothing().given(userRepository).subtractNumOfRecommendInIds(userIds);

        // then
        assertDoesNotThrow(() -> themeMapService.deleteThemeMap(themeMapId));
        assertThat(themeMap.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("테마 장소 삭제")
    void deleteThemeLocation() throws Exception {
        // given
        user.addNumOfRecommend(); // testing subtraction

        ThemeMap themeMap = createThemeMap();
        ThemeLocation themeLocation = createThemeLocation(user, themeMap, location);
        Long themeLocationId = 100L;

        given(themeLocationRepository.findWithUserById(anyLong())).willReturn(Optional.of(themeLocation));
        willDoNothing().given(themeLocationRepository).delete(themeLocation);

        // then
        assertDoesNotThrow(() -> themeMapService.deleteThemeLocation(themeLocationId));
        assertThat(user.getNumOfRecommend()).isEqualTo(0);
    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .role(Role.USER)
                .build();
    }

    private ThemeMap createThemeMap() {
        return ThemeMap.builder()
                .name("test-map")
                .user(user)
                .icon(icon)
                .categories(List.of(Category.CAFE, Category.BAR))
                .build();
    }

    private ThemeLocation createThemeLocation(User who, ThemeMap themeMap, Location where) {
        return ThemeLocation.builder()
                .user(who)
                .themeMap(themeMap)
                .location(where)
                .build();
    }

    private Location createLocation(String name) {
        return Location.builder()
                .name(name)
                .build();
    }
}