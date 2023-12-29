package com.jms.alertmessaging.dto.student;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class DepartmentKeywordsDto {
    private String department;
    private Set<String> keywords;
}
