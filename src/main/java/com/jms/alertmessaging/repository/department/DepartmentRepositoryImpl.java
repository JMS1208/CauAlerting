package com.jms.alertmessaging.repository.department;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.department.QDepartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DepartmentRepositoryImpl extends QuerydslRepositorySupport implements DepartmentRepository {

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

    public DepartmentRepositoryImpl() {
        super(Department.class);
    }

    @Override
    public Optional<Department> findDepartmentById(Long id) {

        QDepartment qDepartment = QDepartment.department;

        Department department = from(qDepartment).where(qDepartment.id.eq(id)).fetchOne();

        return Optional.ofNullable(department);
    }
}
