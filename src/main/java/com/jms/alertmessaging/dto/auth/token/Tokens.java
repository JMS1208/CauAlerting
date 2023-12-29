package com.jms.alertmessaging.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tokens {
    private Token accessToken;
    private Token refreshToken;
}
