package com.jinjjaseoul.auth.oauth2;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, apple
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2Provider oAuth2Provider = OAuth2Provider.of(registrationId, userNameAttributeName, attributes);

        if (oAuth2Provider == null) {
            throw new IllegalStateException("OAuth2 요청 형식이 잘못되었습니다.");
        }

        User user = saveOrUpdate(oAuth2Provider);
        saveUserInfoInSession(session, user);

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .imageUrl(user.getImageUrl())
                .role(user.getRole())
                .provider(user.getProvider())
                .attributes(attributes)
                .build();
    }

    private User saveOrUpdate(OAuth2Provider oAuth2Provider) {
        Optional<User> findUser = userRepository.findByEmail(oAuth2Provider.getEmail());
        User user;

        if (findUser.isEmpty()) {
            user = User.builder()
                    .email(oAuth2Provider.getEmail())
                    .password(null)
                    .name(oAuth2Provider.getName())
                    .imageUrl(oAuth2Provider.getImageUrl())
                    .role(Role.USER)
                    .provider(oAuth2Provider.getProvider())
                    .build();

        } else user = findUser.get().update(oAuth2Provider.getEmail(), oAuth2Provider.getName(), oAuth2Provider.getImageUrl(), oAuth2Provider.getProvider());

        return user;
    }

    private void saveUserInfoInSession(HttpSession session, User user) {
        SessionUser sessionUser = new SessionUser(user.getEmail(), user.getName(), user.getImageUrl());
        session.setAttribute("userInfo", sessionUser);
    }
}