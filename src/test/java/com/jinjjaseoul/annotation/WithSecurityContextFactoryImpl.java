package com.jinjjaseoul.annotation;

import com.jinjjaseoul.auth.model.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithSecurityContextFactoryImpl implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser withAuthUser) {
        UserPrincipal userPrincipal = new UserPrincipal(withAuthUser.email(), withAuthUser.role());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}