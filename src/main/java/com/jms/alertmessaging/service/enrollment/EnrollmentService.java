package com.jms.alertmessaging.service.enrollment;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.student.Student;

import java.util.Set;

public interface EnrollmentService {
    public void saveEnrollments(Student student, Set<Department> departments);

}
