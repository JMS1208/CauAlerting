package com.jms.alertmessaging.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class TokenRefreshResponseDto {
    private String accessToken;
    private Date accessTokenExpiredAt;
    private String refreshToken;
    private Date refreshTokenExpiredAt;

    public static TokenRefreshResponseDto fromTokens(Tokens tokens) {
        TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto();

        responseDto.setAccessToken(tokens.getAccessToken().getToken());
        responseDto.setRefreshToken(tokens.getRefreshToken().getToken());

        responseDto.setRefreshTokenExpiredAt(tokens.getRefreshToken().getExpiredAt());
        responseDto.setAccessTokenExpiredAt(tokens.getAccessToken().getExpiredAt());

        return responseDto;
    }
}
