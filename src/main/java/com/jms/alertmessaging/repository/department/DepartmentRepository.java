package com.jms.alertmessaging.repository.department;

import com.jms.alertmessaging.entity.department.Department;

import java.util.Optional;

public interface DepartmentRepository {

    //학부 찾기
    public Optional<Department> findDepartmentById(Long id);
}
