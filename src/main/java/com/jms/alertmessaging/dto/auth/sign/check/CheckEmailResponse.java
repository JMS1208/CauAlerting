package com.jms.alertmessaging.dto.auth.sign.check;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckEmailResponse {
    private boolean emailExisted;
}
