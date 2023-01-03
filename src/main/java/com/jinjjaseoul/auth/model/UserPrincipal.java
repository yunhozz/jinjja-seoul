package com.jinjjaseoul.auth.model;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class UserPrincipal implements UserDetails, OAuth2User {

    /*
     * @AuthenticationPrincipal 로 불러온 UserPrincipal 의 user 는 준영속 상태이다.
     * 따라서, user 의 변경이 필요한 부분은 userPrincipal 에서 user 를 꺼내는 것이 아니라, id 값을 이용하여 직접 user 를 호출한다.
     */
    private final User user;
    private Map<String, Object> attributes;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 테스트용 인증 유저 생성
    public UserPrincipal(String email, Role role) {
        user = User.builder()
                .email(email)
                .role(role)
                .build();
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public String getIntroduction() {
        return user.getIntroduction();
    }

    public Role getRole() {
        return user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            add(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        }};
    }

    public Provider getProvider() {
        return user.getProvider();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}