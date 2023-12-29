package com.jms.alertmessaging.security.handler.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//접근 권한이 없는 리소스에 접근하는 경우 던지는 AccessDeniedException 을 핸들링할 때
//403 - Forbidden
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.info("[handle] 접근이 막혔을 경우 경로 리다이렉트");
        //TODO - 나중에 exception 매핑 추가
        response.sendRedirect("/exception");
    }
}
