package com.jinjjaseoul.auth.model;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.user.dto.UserResponseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final String introduction;
    private final Long iconId;
    private final Role role;
    private final Provider provider;
    private Map<String, Object> attributes;

    // UserDetailsServiceImpl
    public UserPrincipal(UserResponseDto userResponseDto) {
        id = userResponseDto.getId();
        email = userResponseDto.getEmail();
        password = userResponseDto.getPassword();
        name = userResponseDto.getName();
        introduction = userResponseDto.getIntroduction();
        iconId = userResponseDto.getIconId();
        role = userResponseDto.getRole();
        provider = userResponseDto.getProvider();
    }

    // OAuth2UserServiceImpl
    public UserPrincipal(UserResponseDto userResponseDto, Map<String, Object> attributes) {
        id = userResponseDto.getId();
        email = userResponseDto.getEmail();
        password = userResponseDto.getPassword();
        name = userResponseDto.getName();
        introduction = userResponseDto.getIntroduction();
        iconId = userResponseDto.getIconId();
        role = userResponseDto.getRole();
        provider = userResponseDto.getProvider();
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Long getIconId() {
        return iconId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            add(new SimpleGrantedAuthority(role.getAuthority()));
        }};
    }

    public Provider getProvider() {
        return provider;
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