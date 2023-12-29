package com.jms.alertmessaging.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class Token {
    public String token;
    public Date expiredAt;
}
