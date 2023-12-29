package com.jms.alertmessaging.dto.auth.sign.check;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckEmailResponseDto {
    private boolean emailExisted;
}
