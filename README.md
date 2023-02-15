# 💁🏻‍♂️ 프로젝트 소개

---

![103035338_1191239987884130_5392362461927655417_n.jpg](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/103035338_1191239987884130_5392362461927655417_n.jpg)

진짜서울([https://jinjja-seoul.com](https://jinjja-seoul.com/)) 웹 서비스를 참고하며 재구성한 클론코딩 프로젝트입니다.  단순히 장소를 검색하고 저장하는 지도 서비스가 아닌, 테마를 만들고 장소를 공유하는 과정을 통한 **집단지성 장소 추천 커뮤니티 플랫폼**입니다.

이 서비스는 크게 **테마 지도**와 **큐레이션 지도**로 유형이 나뉩니다. **테마 지도**는 테마별로 큐레이터마다 하나의 장소를 추천하는 지도이며, **큐레이션 지도**는 자신이 원하는 테마를 생성하고 자유롭게 장소를 추천할 수 있는 지도입니다.

지도 위의 하트 아이콘을 클릭하면 지도에 **‘좋아요’**를 할 수 있습니다. 자신이 만든 큐레이션 지도들의 하트 수가 늘어나면 진짜서울 메인 페이지의 추천 큐레이터에 소개됩니다.

운영자가 **직접** 지도의 검색 조건을 업데이트 하는 방식을 채택하여 사용자에게 보다 세밀한 검색 서비스를 제공할 수 있습니다. 이로 인해 다양한 검색 조건으로 원하는 테마, 큐레이션 지도를 찾을 수 있으며, 이를 통해 원하는 정보를 손쉽게 얻을 수 있습니다.

# 🐱 GitHub

---

[https://github.com/yunhozz/jinjja-seoul](https://github.com/yunhozz/jinjja-seoul)

# 📊 프로젝트 전체 구조

---

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled.png)

# 🛠️ 사용 기술 및 라이브러리

---

- Java (JDK 11)
- Spring Boot 2.7
- Spring MVC
- Spring Data JPA
- Spring Security 5
- Spring Rest Docs
- QueryDSL
- MySQL
- MariaDB
- Redis
- JWT (jjwt)
- OAuth 2.0 (client)
- BDD(Behavior-Driven Development)
- AWS EC2 & RDS
- JavaScript
- jQuery

# 📂 디렉토리 구조

> 도메인형 패키지 구조를 채택하여 이에 맞춰 프로젝트를 구성하였습니다.
> 

---

```
\---src
    +---docs
    |   \---asciidoc
    +---main
    |   +---java
    |   |   \---com
    |   |       \---jinjjaseoul
    |   |           +---auth
    |   |           |   +---handler
    |   |           |   +---jwt
    |   |           |   +---model
    |   |           |   \---oauth2
    |   |           +---common
    |   |           |   +---dto
    |   |           |   +---enums
    |   |           |   +---exception
    |   |           |   \---utils
    |   |           +---config
    |   |           +---domain
    |   |           |   +---bookmark
    |   |           |   |   +---controller
    |   |           |   |   +---dto
    |   |           |   |   +---model
    |   |           |   |   |   +---entity
    |   |           |   |   |   \---repository
    |   |           |   |   \---service
    |   |           |   |       \---exception
    |   |           |   +---icon
    |   |           |   |   +---controller
    |   |           |   |   \---model
    |   |           |   +---location
    |   |           |   |   +---controller
    |   |           |   |   +---dto
    |   |           |   |   |   +---query
    |   |           |   |   |   +---request
    |   |           |   |   |   \---response
    |   |           |   |   +---model
    |   |           |   |   |   +---entity
    |   |           |   |   |   \---repository
    |   |           |   |   |       \---location
    |   |           |   |   \---service
    |   |           |   |       \---exception
    |   |           |   +---map
    |   |           |   |   +---controller
    |   |           |   |   +---dto
    |   |           |   |   |   +---query
    |   |           |   |   |   \---request
    |   |           |   |   +---model
    |   |           |   |   |   +---entity
    |   |           |   |   |   \---repository
    |   |           |   |   |       \---map
    |   |           |   |   \---service
    |   |           |   |       \---exception
    |   |           |   \---user
    |   |           |       +---controller
    |   |           |       +---dto
    |   |           |       |   +---query
    |   |           |       |   +---request
    |   |           |       |   \---response
    |   |           |       +---model
    |   |           |       |   \---repository
    |   |           |       \---service
    |   |           |           \---exception
    |   |           \---interfaces
    |   |               +---controller
    |   |               \---form
    |   \---resources
    |       +---static
    |       |   +---docs
    |       |   \---js
    |       \---templates
```

# ❓ 프로젝트 주요 특징

> 프로젝트의 주요 특징들과 각 기술의 적용 이유를 작성하였습니다.
> 

---

1. **모놀리식(Monolithic)** 구조입니다.
2. **Spring Data JPA**를 사용하였습니다.
3. **도메인형 패키지 구조**로 구성되었습니다.
4. **JWT**와 **OAuth2.0**을 함께 사용한 인증 방식입니다.
5. **Redis** 캐시 클러스터를 사용했습니다.
6. **BDD** 개발 방법론을 채택하였습니다.

### 계층형 vs 도메인형 패키지 구조

<aside>
💡 도메인형 패키지 구조 채택 이유

- 계층형 구조가 빠르게 파악하기 쉽지만, 개인적으로 하나의 패키지에 클래스들이 과도하게 몰려있는 것을 선호하지 않기 때문입니다.
- ‘진짜서울’ 은 추후 서비스를 확장할 가능성이 충분하다고 생각하였습니다. 최근에는 ‘바운더리’라는 빅데이터 기반 서비스를 출시하여 사용자에게 더 많은 유익한 정보를 제공하고 있습니다.
- 명확한 기준을 토대로 제공되는 기능들을 정의할 수 있다고 판단하였습니다.
</aside>

- 계층형 구조
    - 프로젝트의 전체적인 구조를 한눈에 파악하기 쉽습니다.
    - 하나의 디렉토리에 클래스들이 과다하게 몰릴 우려가 있습니다.
    - 모듈 단위로 분리 시 어려움이 있습니다.
- 도메인형 구조
    - 각 도메인 별 관련 클래스들이 있어 응집도가 증가하게 됩니다.
    - 모듈 단위로 분리할 때 유리합니다.
    - 패키지 간 순환 참조 우려가 있습니다.

### JWT 인증 방식

- 세션에 저장하는 방식은 **서버의 자원**을 사용하는 것이기 때문에 접속자가 많을 경우 서버 자원이 부족해질 수도 있고, 서버 장애시 정보가 소실되면서 사용자에게 **부정적인 경험**을 주기 쉽습니다.
- **JSON** 객체를 통해 두 당사자 간 정보를 보안이 적용된 안전한 방식으로 전달하기 때문입니다. 또한, JSON은 인코딩 되었을 때 크기가 작으므로 HTML 이나 HTTP 를 이용해 정보를 넘기기에 좋고, 인터넷 스케일에서 기기에 상관없이 어디서든 쓰이기에 **호환성**이 좋기 때문입니다.
- JWT 토큰을 `Authorization` 헤더에 담아 보내게 된다면 쿠키를 사용하지 않으므로 **CORS**(Cross-Origin Resource Sharing) 이슈를 피할 수 있습니다.

### Redis를 사용한 이유?

- 주된 이유는 JWT 토큰 인증 방식을 적용함으로써 토큰을 수시로 재발급하는데 사용되는 **refresh token**을 Redis에 저장해보고 사용해보는 기회를 가져보고자 사용하게 되었습니다.
- 앞서 말한 JWT 토큰을 서버 혹은 클라이언트 측에 저장할 시 사후 처리가 곤란하게 되므로, 비교적 안정한 세션 클러스터를 채택하게 되었습니다.
- **인메모리 NoSQL 형식**의 저장소이므로 문법적으로 사용하기 쉽고, **disk I/O 작업**이 발생하지 않아 속도가 빠르며, **휘발성**이기 때문입니다.
- `String` 뿐만 아니라 `Set`, `Hash`, `List` 등 **다양한 데이터 형식**을 지원하므로, 프로젝트 내에서 지도 생성 시 관련된 임시 데이터들을 map 형식으로 저장하고 꺼내오는데 용이했습니다.

### BDD와 단위 테스트

<aside>
💡 **BDD**(Behavior-Driven Development) 는 ‘행위 주도 개발’을 뜻하며, 기본적으로 **given/when/then** 의 패턴을 추구합니다.
**BDDMockito**는 Mockito를 상속한 클래스이고, BDD를 사용하여 테스트코드를 작성할 때, 시나리오에 맞게 테스트 코드가 읽힐 수 있도록 이름을 변경한 프레임워크입니다.
따라서, BDD를 도입하여 단위 테스트의 가독성을 더 높이기 위해 BDDMockito를 사용하게 되었습니다.

</aside>

- Mockito

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    User testUser;

    @Test
    void accessProfile_userId_valid_return_dto() {
        // given
        // Mockito.when() 때문에 가독성이 떨어져 헷갈릴 수 있다.
        when(userRepository.findByUserId(anyString())).thenAnswer((e) -> {
            testUser = new User((String) e.getArgument(0), "email");
            return Optional.of(testUser);
        });

        // when
        ProfileInfo result = userService.accessProfile("userId");

        // then
        assertEquals(testUser.getEmail(), result.getEmail());
    }
}
```

- BDDMockito

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    User testUser;

    @Test
    void accessProfile_userId_valid_return_dto() {
        // given
        // BDDMockito.given() 을 사용하여 가독성을 높인다.
        given(userRepository.findByUserId(anyString())).willReturn((e) -> {
            testUser = new User((String) e.getArgument(0), "email");
            return Optional.of(testUser);
        });

        // when
        ProfileInfo result = userService.accessProfile("userId");

        // then
        assertEquals(testUser.getEmail(), result.getEmail());
    }
}
```

# 🎨 구현 API

> 프로젝트의 전체 api 목록입니다.
> 

---

### API 목록

[API Document](http://3.35.86.55:8080/docs/index.html)

- 유저
    - 열혈 큐레이터 리스트 조회
    - 자신의 정보 조회
    - 특정 유저의 정보 조회
    - 회원가입
    - 프로필 업데이트
    - 로그인
    - JWT 토큰 재발급
    - 로그아웃
    - 회원탈퇴
- 지도
    - 테마 지도
        - 추천 리스트 조회
        - 최신 리스트 조회
        - 인기 리스트 조회
        - 단건 조회
        - 생성
        - 장소 추가
        - 검색 조건 업데이트
        - 지도 삭제
        - 특정 장소 삭제
    - 큐레이션 지도
        - 리스트 조회
        - 단건 조회
        - 생성
        - 장소 추가
        - 검색 조건 업데이트
        - 정보 수정
        - 지도 삭제
        - 특정 장소 삭제
    - 지도 검색
- 북마크
    - 장소 북마크 리스트 조회
    - 장소 북마크 생성
    - 큐레이션 지도 좋아요 기능
    - 장소 북마크 취소
    - 큐레이션 지도 좋아요 취소
- 댓글
    - 작성
    - 삭제
- 이미지
    - 특정 장소의 이미지 리스트 조회
    - 단건 조회
    - 업로드

# 🗂️ 데이터베이스 ERD

> 직접 ERD를 만들어보며 테이블 간 관계를 설정하고, 테이블 속성 및 세부 사항 등을 정리하였습니다.
> 

---

### 링크

[ERD Cloud - Jinjja Seoul](https://www.erdcloud.com/d/zL658uhXQy6wvCRee)

### 전체 데이터베이스

![jinjja-seoul.png](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/jinjja-seoul.png)

### 유저

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%201.png)

### 지도

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%202.png)

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%203.png)

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%204.png)

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%205.png)

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%206.png)

### 북마크

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%207.png)

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%208.png)

### 댓글

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%209.png)

### 이미지

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%2010.png)

### 아이콘

![Untitled](../../진짜서울%20bea0ffa30ac24316ad09dbfefc5f53a5/Untitled%2011.png)

# 😆 데모 사이트

> 직접 JavaScript, jQuery를 사용해보며 api가 어떤 식으로 호출되며, 화면이 어떤 flow로 흘러가는지 테스트 용으로 만든 데모 사이트입니다. 로직이나 디자인적으로 많이 미흡한 점을 감안해주시면 감사하겠습니다. 😭
> 

---

[진짜서울](http://3.35.86.55:8080)

# 🦥 프로젝트 회고

> 프로젝트를 진행하면서 얻은 깨달음이나 어려움 또는 아쉬웠던 점을 기록하였습니다.
> 

---

- JWT와 OAuth2.0을 함께 적용하는 것이 까다로웠습니다. 처음엔 OAuth2.0의 내부 구조와 인증 절차가 어떻게 흘러가는지 감이 안잡혀 **WebFlux**를 사용하여 직접 메소드를 짜보며 최대한 이해하려고 노력했습니다. 로그인 성공시와 실패시 핸들러를 구성하는 과정이 복잡했습니다.

```java
@Transactional
public TokenResponseDto login(String registrationId, String code) {
    ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId);
    OAuth2AccessTokenResponse tokenResponse = getToken(code, clientRegistration);

    return new TokenResponseDto(
            "Bearer ",
            tokenResponse.getAccessToken().getTokenValue(),
            tokenResponse.getRefreshToken().getTokenValue(),
            tokenResponse.getRefreshToken().getExpiresAt().toEpochMilli()
    );
}

private OAuth2AccessTokenResponse getToken(String code, ClientRegistration clientRegistration) {
    return WebClient.create()
            .post()
            .uri(clientRegistration.getProviderDetails().getTokenUri())
            .headers(header -> {
                header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            })
            .bodyValue(tokenRequest(code, clientRegistration))
            .retrieve()
            .bodyToMono(OAuth2AccessTokenResponse.class)
            .block();
}

private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration clientRegistration) {
    LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("code", code);
    data.add("grant_type", "authorization_code");
    data.add("client_id", clientRegistration.getClientId());
    data.add("redirect_uri", clientRegistration.getRedirectUri());

    return data;
}
```

- **추상클래스**의 사용으로 인하여 QueryDSL을 사용하여 쿼리를 짜는데 많은 시행착오를 겪었습니다. 특히, 지도의 종류를 `dtype`으로 구분하게 되는데 두 지도 사이의 조건이 겹치지 않으면서 쿼리를 구성하는 것에서 많은 어려움을 겪었습니다.

```java
@Entity
@Getter
// @DiscriminatorColumn 사용 x
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Map extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ...

		// QueryDSL 을 위해 @DiscriminatorColumn 대신 직접 명시 -> TM, CM 로 구분
    @Column(insertable = false, updatable = false)
    private String dtype;
}

@Entity
@Getter
@DiscriminatorValue("CM") // 구분자 표시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationMap extends Map {

}

@Repository
@RequiredArgsConstructor
public class MapCustomRepositoryImpl implements MapCustomRepository {

    private final JPAQueryFactory queryFactory;

    ...

    // curationMap의 제목에서 해당 키워드가 존재하는지 검증
    private BooleanExpression curationMapKeywordEq(String keyword) {
        return StringUtils.hasText(keyword) ? curationMap.dtype.eq("CM").and(curationMap.name.contains(keyword)) : null;
    }
}
```

- Service 레이어에서 `getter`를 최소화하기 위해 고민했습니다. 객체의 요소를 직접 꺼내와서 로직을 수행하는 것이 아니라, 객체에게 물음을 던져 **책임을 인가**한다는 점에서 객체지향의 의미를 다시 한번 되새길 수 있었습니다.

```java
// userRepository에서 id로 user 조회
User user = userRepository.getReferenceById(userId);

// themeMap에서 직접 user을 꺼내와 조회한 user와 비교 (x)
if (themeMap.getUser().equals(user)) {
    ThemeLocation themeLocation = themeLocationRepository.findByUserAndThemeMap(user, themeMap)
            .orElseThrow(ThemeLocationNotFoundException::new);
    themeLocation.updateLocation(location);
}

// themeMap의 isMadeByUser() 메소드를 호출하여 책임을 위임 (o)
if (themeMap.isMadeByUser(user)) {
    ThemeLocation themeLocation = themeLocationRepository.findByUserAndThemeMap(user, themeMap)
            .orElseThrow(ThemeLocationNotFoundException::new);
    themeLocation.updateLocation(location);
}
```

- **벌크성 쿼리**와 **영속성 컨텍스트(=1차 캐시)** 사이의 상관관계에 대해 알게 되었습니다. 벌크성 쿼리의 `clearAutomatically = true` **옵션에 의해 영속성 컨텍스트를 무시하고 DB에 바로 벌크성 쿼리를 날리고 1차 캐시를 **초기화**합니다. 이로써 트랜잭션이 강제로 종료되게 되고 일반 업데이트 쿼리가 무시되게 됩니다. 따라서, 벌크성 쿼리는 따로 트랜잭션을 구성해야 한다는 것을 깨달았습니다.

```java
/* 각 repository에 정의된 벌크성 쿼리 */
public interface SomethingRepository extends JpaRepository<Something, Long> {
    // 해당 테마 지도의 테마 장소 전체 삭제
    @Modifying(clearAutomatically = true)
    @Query("delete from ThemeLocation tl where tl.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> themeLocationIds);

    // 해당 테마 지도의 유저들의 추천수 -1
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.numOfRecommend = u.numOfRecommend - 1 where u.id in :ids")
    void subtractNumOfRecommendInIds(@Param("ids") List<Long> userIds);
}

/* 변경 전 */
@Transactional
public void deleteThemeMap(Long themeMapId) {
    ThemeMap themeMap = findThemeMap(themeMapId);
    List<Long> themeLocationIds = themeLocationRepository.findIdsByThemeMapId(themeMapId);
    List<Long> userIds = userRepository.findIdsByThemeLocationIds(themeLocationIds);

    themeMap.delete(); // 일반 update 쿼리(soft delete) -> 무시됨
    themeLocationRepository.deleteAllByIds(themeLocationIds); // 벌크성 쿼리
    userRepository.subtractNumOfRecommendInIds(userIds); // 벌크성 쿼리
}

/* 변경 후 */
@Transactional
public void deleteThemeMap(Long themeMapId) {
    ThemeMap themeMap = findThemeMap(themeMapId);
    themeMap.delete(); // 일반 update 쿼리 (soft delete)
    updateTablesByDeletingThemeMap(themeMapId); // 따로 트랜잭션 구성
}

@Transactional
protected void updateTablesByDeletingThemeMap(Long themeMapId) {
    List<Long> themeLocationIds = themeLocationRepository.findIdsByThemeMapId(themeMapId);
    List<Long> userIds = userRepository.findIdsByThemeLocationIds(themeLocationIds);
		// 벌크성 쿼리
    themeLocationRepository.deleteAllByIds(themeLocationIds);
    userRepository.subtractNumOfRecommendInIds(userIds);
}
```

- `BDDMockito`를 활용하여 Rest Docs를 작성해볼 수 있었습니다. 이미지 업로드 api 테스트할 때 `MultipartFile`을 `Mock`으로 처리할 수 있다는 것을 알게 되었습니다.

```java
// given
MockMultipartFile multipartFile = new MockMultipartFile("images", "test.jpeg", MediaType.IMAGE_JPEG_VALUE, "<<jpeg data>>".getBytes());

// when
ResultActions result = mockMvc.perform(multipart("/api/images/upload")
        .file(multipartFile)
);
```

- **Redis**를 활용하여 JWT 재발급 토큰의 저장, 로그아웃 처리, 지도 생성시 임시 데이터 저장을 구현할 수 있었습니다. 다양한 데이터 형식으로 간편하게 I/O 작업을 할 수 있어 큰 메리트를 느꼈습니다.
- AWS **EC2**와 **RDS** 사용법에 대해 알게 되었으며, 이를 사용하여 프로젝트를 직접 배포해 보았습니다. 처음 배포시 EC2 서버에 Redis를 설치하지 않는 바람에 사이트가 동작하지 않아 많이 헤메었습니다.
- **jQuery**로 직접 api를 호출하는 방법을 배우게 되었습니다. 하지만, 인증이 필요한 api의 경우 발급받은 JWT 토큰을 헤더에 **상시** 적용하는 부분을 jQuery로 구현하기가 애매했습니다.
- 카카오맵 open api를 사용해야 하는 부분이 있었는데 이를 활용하지 못했다는 점에서 아쉬움이 남았습니다. 예를 들어, 지도 상에서 특정 장소를 선택했을 때 이 정보를 서버 측으로 보내는 과정이 생소했고 도저히 감이 잡히지 않았습니다. 다음에 기회가 된다면 프론트 분과 협업을 통해 이러한 부분에 대해 배우고 싶습니다.

# 🙇🏻‍♂️ 감사합니다!
