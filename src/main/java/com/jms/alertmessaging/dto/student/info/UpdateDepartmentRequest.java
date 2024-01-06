package com.jms.alertmessaging.dto.student.info;

import lombok.Data;

@Data
public class UpdateDepartmentRequest {
    private long departmentId;
    private boolean select;
}
