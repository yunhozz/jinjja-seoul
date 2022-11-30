package com.jinjjaseoul.auth.model;

import com.jinjjaseoul.common.enums.Role;
import lombok.Builder;
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
    private final String imageUrl;
    private final Role role;
    private Map<String, Object> attributes;

    @Builder
    private UserPrincipal(Long id, String email, String password, String name, String imageUrl, Role role, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            add(new SimpleGrantedAuthority(role.getAuthority()));
        }};
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

    public String getImageUrl() {
        return imageUrl;
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