package com.jms.alertmessaging.filterchain.jwt;

import com.jms.alertmessaging.component.jwt.JwtProvider;
import com.jms.alertmessaging.config.security.SecurityConfig;
import com.jms.alertmessaging.data.token.Token;
import com.jms.alertmessaging.exception.auth.CookieNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();

        if (Arrays.stream(SecurityConfig.PUBLIC_URLS).noneMatch(url -> pathMatcher.match(url, requestURI))) {

            try {
                LOGGER.info("[doFilterInternal] 쿠키로 부터 토큰 추출하기");

                String accessToken = jwtProvider.resolveFromCookie(JwtProvider.ACCESS_TOKEN, request);

                LOGGER.info("[doFilterInternal] accessToken 값 추출 완료. token: {}", accessToken);

                LOGGER.info("[doFilterInternal] accessToken 값 유효성 체크 시작");

                if (accessToken != null && jwtProvider.isValidTokenWithExpiration(accessToken)) {
                    //스프링 시큐리티 컨텍스트에 등록
                    Authentication authentication = jwtProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    LOGGER.info("[doFilterInternal] token 값 유효성 체크 완료");
                }
            } catch (CookieNotFoundException e1) {
                try {
                    //액세스 토큰이 없다면, 리프레시 토큰으로 인증 후 갱신
                    String refreshToken = jwtProvider.resolveFromCookie(JwtProvider.REFRESH_TOKEN, request);

                    if (refreshToken != null && jwtProvider.isValidTokenWithExpiration(refreshToken)) {
                        //다시 액세스 토큰 생성해서 주입
                        Token accessToken = jwtProvider.recreateAccessToken(refreshToken);

                        jwtProvider.setTokenToCookie(accessToken.key, accessToken.value, JwtProvider.ACCESS_TOKEN_COOKIE_DURATION, response);

                        //스프링 시큐리티 컨텍스트에 등록
                        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        LOGGER.info("[doFilterInternal] token 값 유효성 체크 완료");
                    }
                } catch (CookieNotFoundException e2) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(e2.getMessage());
                    response.getWriter().flush();
                    return;
                }
            } catch (Exception e3) {
                throw new ServletException(e3);
            }
        }

        filterChain.doFilter(request, response);
    }


}
