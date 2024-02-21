package com.jms.alertmessaging.service.enrollment;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.enrollment.EnrollmentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentJpaRepository enrollmentJpaRepository;

    @Override
    public void saveEnrollments(Student student, Collection<Department> departments) {

        log.info("[saveEnrollments] student: {}, departments: {}", student, departments);
        Set<Enrollment> enrollments = new HashSet<>();

        for(Department department: departments) {
            Enrollment enrollment = new Enrollment();
            enrollment.setDepartment(department);
            enrollment.setStudent(student);
            enrollments.add(enrollment);
        }

        List<Enrollment> savedEnrollments = enrollmentJpaRepository.saveAll(enrollments);
        log.info("[saveEnrollments] 저장된것: {} ", savedEnrollments);
    }

    @Override
    public void saveEnrollment(Student student, Department department) {

        Enrollment enrollment = new Enrollment();
        enrollment.setDepartment(department);
        enrollment.setStudent(student);

        Enrollment savedEnrollment = enrollmentJpaRepository.save(enrollment);
        log.info("[saveEnrollment] 저장된 것: {} ", savedEnrollment);
    }
}
