package com.jms.alertmessaging.dto.student;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StudentInfoBundleDto {
    private String email;
    private Map<Long,DepartmentKeywordsDto> departmentKeywords;

}
