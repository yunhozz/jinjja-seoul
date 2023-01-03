package com.jinjjaseoul.annotation;

import com.jinjjaseoul.common.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithSecurityContextFactoryImpl.class)
public @interface WithAuthUser {

    String email() default "test@gmail.com";
    Role role();
}