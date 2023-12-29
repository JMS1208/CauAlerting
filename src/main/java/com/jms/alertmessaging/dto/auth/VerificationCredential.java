package com.jms.alertmessaging.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerificationCredential {
    private String username;
    private String code;
    private LocalDateTime expiredAt;
}
