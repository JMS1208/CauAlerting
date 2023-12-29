package com.jms.alertmessaging.dto.auth.sign.code.verify;

import lombok.Data;

@Data
public class VerifyCodeRequestDto {
    private String email;
    private String code;
}
