package com.jms.alertmessaging.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerificationDto {
    private String username;
    private LocalDateTime expiredAt;
}
