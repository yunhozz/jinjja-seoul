package com.jinjjaseoul.auth.oauth2;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;

    @Override
    @Transactional
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
        UserResponseDto userResponseDto = UserConverter.convertToDto(user);

        return new UserPrincipal(userResponseDto, attributes);
    }

    private User saveOrUpdate(OAuth2Provider oAuth2Provider) {
        Optional<User> findUser = userRepository.findByEmail(oAuth2Provider.getEmail());
        User user;

        if (findUser.isEmpty()) {
            Icon icon = randomIcon();
            iconRepository.save(icon);

            user = User.builder()
                    .email(oAuth2Provider.getEmail())
                    .password(null)
                    .name(oAuth2Provider.getName())
                    .introduction(null)
                    .icon(icon)
                    .role(Role.USER)
                    .provider(oAuth2Provider.getProvider())
                    .build();

            userRepository.save(user);

        } else {
            user = findUser.get().updateInfo(oAuth2Provider.getEmail(), oAuth2Provider.getName(), oAuth2Provider.getProvider());
        }

        return user;
    }

    private Icon randomIcon() {
        long iconSize = iconRepository.count();
        Random random = new Random(iconSize);

        return iconRepository.getReferenceById(random.nextLong());
    }
}