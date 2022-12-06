package com.jinjjaseoul.auth.model;

import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.domain.user.dto.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        UserResponseDto userResponseDto = UserConverter.convertToDto(user);

        return new UserPrincipal(userResponseDto);
    }
}