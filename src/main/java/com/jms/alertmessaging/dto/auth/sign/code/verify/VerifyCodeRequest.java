package com.jms.alertmessaging.dto.auth.sign.code.verify;

import lombok.Data;

@Data
public class VerifyCodeRequest {
    private String email;
    private String code;
}
