package com.jms.alertmessaging.component.jwt;

import com.jms.alertmessaging.data.token.Token;
import com.jms.alertmessaging.data.token.Tokens;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface JwtProvider {

    String ACCESS_TOKEN = "accessToken";
    String REFRESH_TOKEN = "refreshToken";
    String ROLES = "roles";
    String SET_COOKIE = "Set-Cookie";

    //1시간
    int ACCESS_TOKEN_COOKIE_DURATION = 60 * 60;

    //1일
    int REFRESH_TOKEN_COOKIE_DURATION = 60 * 60 * 24;

    long ACCESS_TOKEN_EXPIRY = ACCESS_TOKEN_COOKIE_DURATION * 1000L;
    long REFRESH_TOKEN_EXPIRY = REFRESH_TOKEN_COOKIE_DURATION * 1000L;

    //토큰이 유효한지 + 만료되었는지 검증
    boolean isValidTokenWithExpiration(String token);

    //토큰이 유효한지 검증
    boolean isValidTokenWithoutExpiration(String token);

    //Json Web Token 중 페이로드에 들어가는 정보
    Claims createClaims(String userEmail, List<String> roles);

    //액세스 토큰 생성
    Token createAccessToken(String userEmail, List<String> roles);

    //리프레시 토큰 생성
    Token createRefreshToken(String userEmail, List<String> roles);

    //액세스 토큰 갱신
    Token recreateAccessToken(String refreshToken);

    //토큰으로부터 사용자의 인증정보 가져오기
    Authentication getAuthentication(String token);

    //토큰으로부터 사용자의 이메일 가져오기
    String getUserEmail(String token);

    //토큰으로부터 사용자 권한 가져오기
    List<String> getUserRoles(String token);

    //쿠키에서 값 꺼내기
    String resolveFromCookie(String key, HttpServletRequest request);

    //쿠키 생성
    ResponseCookie createCookie(String key, String value, int maxAge);

    //쿠키에서 토큰 삭제
    void removeTokensFromCookie(HttpServletResponse response);

    //쿠키에 토큰을 주입
    void setTokenToCookie(String key, String value, int duration, HttpServletResponse response);
}
