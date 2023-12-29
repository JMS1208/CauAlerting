package com.jms.alertmessaging.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecaptchaResponse {

    private boolean success;
    private double score;
    private String action;

}