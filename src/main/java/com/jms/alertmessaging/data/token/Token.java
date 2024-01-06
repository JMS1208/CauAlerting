package com.jms.alertmessaging.data.token;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class Token {
    public String key;
    public String value;
    public Date expiredAt;
}
