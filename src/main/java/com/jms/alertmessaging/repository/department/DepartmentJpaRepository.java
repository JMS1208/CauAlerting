package com.jms.alertmessaging.repository.department;

import com.jms.alertmessaging.entity.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DepartmentJpaRepository extends JpaRepository<Department, Long> {

    public Department findById(long id);

    public Set<Department> findByIdIn(Set<Long> ids);

}
