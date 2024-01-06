package com.jms.alertmessaging.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class DepartmentKeywords {
    private Long departmentId;
    private String department;
    private List<String> keywords;
    private Boolean selected;
}
