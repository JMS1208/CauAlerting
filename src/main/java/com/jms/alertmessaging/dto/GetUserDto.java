package com.jms.alertmessaging.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserDto {
    private long userId;
    private String username;
    private boolean alertingStatus;
    private String major;
}
