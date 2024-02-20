package com.jms.alertmessaging.dto.student;

import lombok.Getter;

import java.util.List;

@Getter
public class KeywordDto {

    private Long departmentId;

    private List<String> keywords;
}
