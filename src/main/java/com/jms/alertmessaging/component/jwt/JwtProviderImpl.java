package com.jms.alertmessaging.component.jwt;


import com.jms.alertmessaging.data.token.Token;
import com.jms.alertmessaging.data.token.Tokens;
import com.jms.alertmessaging.exception.auth.CookieNotFoundException;
import com.jms.alertmessaging.exception.auth.InvalidTokenException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtProviderImpl.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final UserDetailsService userDetailsService;

    @Override
    public boolean isValidTokenWithExpiration(String token) {
        try {
            // 토큰이 유효하며 만료되지 않았음
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            LOGGER.info("[isValidTokenWithExpiration] 토큰이 유효함: {}", token);
            return true;
        } catch (ExpiredJwtException ex) {
            // 토큰이 만료된 경우
            LOGGER.info("[isValidTokenWithExpiration] 토큰이 만료됨: {}", token);
            return false;
        } catch (Exception ex) {
            // 토큰이 다른 이유로 유효하지 않은 경우
            LOGGER.info("[isValidTokenWithExpiration] 토큰이 유효하지 않음: {}", token);
            return false;
        }
    }

    @Override
    public boolean isValidTokenWithoutExpiration(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parse(token).getBody();
            return true;
        } catch (Exception ex) {
            // 토큰이 유효하지 않은 경우 (만료 제외)
            return false;
        }
    }

    @Override
    public Claims createClaims(String userEmail, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put(ROLES, roles);
        return claims;
    }

    @Override
    public Token createAccessToken(String userEmail, List<String> roles) {
        LOGGER.info("[createAccessToken] 토큰 생성 시작");

        Token accessToken = createToken(ACCESS_TOKEN, userEmail, roles, ACCESS_TOKEN_EXPIRY);

        LOGGER.info("[createAccessToken] 토큰 생성 완료");

        return accessToken;
    }

    @Override
    public Token createRefreshToken(String userEmail, List<String> roles) {
        LOGGER.info("[createRefreshToken] 토큰 생성 시작");

        Token refreshToken = createToken(REFRESH_TOKEN, userEmail, roles, REFRESH_TOKEN_EXPIRY);

        LOGGER.info("[createRefreshToken] 토큰 생성 완료");

        return refreshToken;
    }

    private Token createToken(String key, String userEmail, List<String> roles, long duration) {
        Claims claims = createClaims(userEmail, roles);

        Date now = new Date();

        Date expiredAt = new Date(now.getTime() + duration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        LOGGER.info("[createToken] 이메일: {}, 토큰: {}", userEmail, token);

        return new Token(key, token, expiredAt);
    }

    @Override
    public Token recreateAccessToken(String refreshToken) throws RuntimeException {

        //refresh 토큰이 유효한지 만료되지 않았는지 확인
        if(!isValidTokenWithExpiration(refreshToken)) throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");

        String userEmail = getUserEmail(refreshToken);

        List<String> roles = getUserRoles(refreshToken);

        return createToken(ACCESS_TOKEN, userEmail, roles, ACCESS_TOKEN_COOKIE_DURATION);
    }

    @Override
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));

        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserEmail: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public String getUserEmail(String token) throws RuntimeException {
        try {
            LOGGER.info("[getUserId] 토큰 기반 회원 구별 정보 추출");
            String info = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody()
                    .getSubject();

            LOGGER.info("[getUserId] 토큰 기반 회원 구별 정보 추출 완료, info: {}", info);

            return info;
        } catch (Exception e) {
            LOGGER.error("토큰 만료", e);
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
    }

    @Override
    public List<String> getUserRoles(String token) throws RuntimeException {
        try {
            LOGGER.info("[getRoles] 토큰 기반 사용자 권한 정보 추출");

            // 토큰 파싱
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

            // 권한 정보 추출
            List<String> roles = claims.get(ROLES, List.class);

            LOGGER.info("[getRoles] 토큰 기반 사용자 권한 정보 추출 완료, roles: {}", roles);

            return roles;
        } catch(Exception e) {
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
    }

    @Override
    public String resolveFromCookie(String key, HttpServletRequest request) throws CookieNotFoundException {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                LOGGER.info("쿠키 찾기: {}", cookie);

                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        throw new CookieNotFoundException("쿠키를 찾을 수 없습니다.");
    }

    @Override
    public ResponseCookie createCookie(String key, String value, int maxAge) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge)
                .build();
    }


    @Override
    public void removeTokensFromCookie(HttpServletResponse response) {
        // 쿠키 즉시 만료시키기
        ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN, null, 0);
        ResponseCookie refreshTokenCookie = createCookie(REFRESH_TOKEN, null, 0);

        response.addHeader(SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(SET_COOKIE, refreshTokenCookie.toString());
    }

    @Override
    public void setTokenToCookie(String key, String value, int duration, HttpServletResponse response) {
        LOGGER.info("[setTokensToCookie] 토큰 주입 {}: {}", key, value);
        ResponseCookie cookie = createCookie(key, value, duration);
        LOGGER.info("[setTokensToCookie] 쿠키 만들었다: {}", cookie);
        response.addHeader(SET_COOKIE, cookie.toString());
    }
}
