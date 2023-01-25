package com.jinjjaseoul.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMsg = null;
        String redirectUrl = "/sign-in?error";

        if (exception instanceof UsernameNotFoundException) {
            errorMsg = "계정이 존재하지 않습니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorMsg = "이메일 또는 비밀번호를 잘못 입력하셨습니다.";
        } else if (exception instanceof SessionAuthenticationException) {
            errorMsg = "중복 로그인";
        }

        log.info(errorMsg);
        request.setAttribute("errorMsg", errorMsg);
        request.getRequestDispatcher(redirectUrl).forward(request, response);
    }
}