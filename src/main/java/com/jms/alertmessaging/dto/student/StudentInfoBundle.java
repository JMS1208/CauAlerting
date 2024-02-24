package com.jms.alertmessaging.dto.student;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentInfoBundle {
    private String email;
    private List<DepartmentKeywords> departmentKeywordsList;
}
