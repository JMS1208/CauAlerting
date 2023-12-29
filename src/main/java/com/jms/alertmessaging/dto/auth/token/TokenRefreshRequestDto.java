package com.jms.alertmessaging.dto.auth.token;

import lombok.Data;

@Data
public class TokenRefreshRequestDto {
    private String refreshToken;
    private String accessToken;
}
