package com.jms.alertmessaging.repository.enrollment;

import com.jms.alertmessaging.entity.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, Long> {
//    Set<Enrollment> findByDepartmentIdIn(Set<Long> departmentIds);

    Set<Enrollment> findByStudentId(Long studentId);
}

