package com.jms.alertmessaging.dto.auth.sign.code.send;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class SendCodeResponse {
    private Date expiredAt;
}
