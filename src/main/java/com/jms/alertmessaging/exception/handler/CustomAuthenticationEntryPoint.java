package com.jms.alertmessaging.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jms.alertmessaging.config.security.SecurityConfig;
import com.jms.alertmessaging.dto.base.ResponseDto;
import io.netty.util.CharsetUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Arrays;


//인증 과정에서 예외가 발생하는 경우 호출
//사용자가 잘못된 이메일이나 비밀번호를 제공했을 때
//인증 토큰이 누락되었을 때
//토큰이 만료되었을 때
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();

        LOGGER.info("[commence] requestURI: {}", requestURI);
        if(Arrays.stream(SecurityConfig.PUBLIC_URLS).noneMatch(url -> pathMatcher.match(url, requestURI))) {

            ResponseDto<String> responseDto = new ResponseDto<>();
            responseDto.setMessage("인증이 실패하였습니다.");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseDto.toJsonString());
            LOGGER.info("[commence] 인증 실패");

        }
    }
}
