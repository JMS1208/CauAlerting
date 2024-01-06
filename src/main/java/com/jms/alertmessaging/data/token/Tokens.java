package com.jms.alertmessaging.data.token;

import com.jms.alertmessaging.data.token.Token;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tokens {
    private Token accessToken;
    private Token refreshToken;
}
