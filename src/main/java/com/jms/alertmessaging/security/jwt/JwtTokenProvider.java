package com.jms.alertmessaging.security.jwt;

import com.jms.alertmessaging.dto.auth.token.Token;
import com.jms.alertmessaging.dto.auth.token.Tokens;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;
    private final long ACCESS_TOKEN_DURATION = 1000L * 60 * 60;
    private final long REFRESH_TOKEN_DURATION = 1000L * 60 * 60 * 24;

    @Value("${jwt.secret}")
    private String secretKey;

    private Claims createClaims(String userEmail, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("roles", roles);
        return claims;
    }
    public Token createAccessToken(String userEmail, List<String> roles) {
        logger.info("[createAccessToken] 토큰 생성 시작");

        Claims claims = createClaims(userEmail, roles);

        Date now = new Date();

        Date expiredAt = new Date(now.getTime() + ACCESS_TOKEN_DURATION);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        logger.info("[createAccessToken] 토큰 생성 완료");

        return new Token(token, expiredAt);
    }


    public Token createRefreshToken(String userEmail, List<String> roles) {
        logger.info("[createRefreshToken] 토큰 생성 시작");

        Claims claims = createClaims(userEmail, roles);

        Date now = new Date();

        Date expiredAt = new Date(now.getTime() + REFRESH_TOKEN_DURATION);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        logger.info("[createRefreshToken] 토큰 생성 완료");

        return new Token(token, expiredAt);
    }

    public Authentication getAuthentication(String token) {
        logger.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));

        logger.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserEmail: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        logger.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .getSubject();

        logger.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출 완료, info: {}", info);

        return info;
    }

    public List<String> getUserRoles(String token) {
        logger.info("[getUserRoles] 토큰 기반 사용자 권한 정보 추출");

        // 토큰 파싱
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        // 권한 정보 추출
        List<String> roles = claims.get("roles", List.class);

        logger.info("[getUserRoles] 토큰 기반 사용자 권한 정보 추출 완료, roles: {}", roles);

        return roles;
    }

    public String resolveToken(HttpServletRequest request) {
        logger.info("[resolveToken] Http 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token) {
        logger.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e) {
            logger.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }

    public Tokens refreshTokens(String accessToken, String refreshToken) throws JwtException {
        try {
            // 토큰에서 이메일과 권한 추출
            String refreshEmail = getUserEmail(refreshToken);
            Set<String> refreshRoles = new HashSet<>(getUserRoles(refreshToken));

            String accessEmail = getUserEmail(accessToken);
            Set<String> accessRoles = new HashSet<>(getUserRoles(accessToken));

            // 이메일과 권한이 일치하는지 확인
            if (!refreshEmail.equals(accessEmail) || !refreshRoles.equals(accessRoles)) {
                throw new JwtException("토큰 정보가 일치하지 않습니다.");
            }

            // 서명 검증 - 만료된 토큰도 검증을 통과하게 설정
            parseJwtWithoutExpirationCheck(refreshToken);
            parseJwtWithoutExpirationCheck(accessToken);

            // 새로운 토큰 생성
            Token newAccessToken = createAccessToken(accessEmail, new ArrayList<>(accessRoles));
            Token newRefreshToken = createRefreshToken(refreshEmail, new ArrayList<>(refreshRoles));

            return new Tokens(newAccessToken, newRefreshToken);
        } catch (JwtException e) {

            throw e;
        }
    }

    private void parseJwtWithoutExpirationCheck(String token) {
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
