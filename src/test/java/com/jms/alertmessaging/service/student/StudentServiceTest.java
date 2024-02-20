package com.jms.alertmessaging.service.student;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.enrollment.EnrollmentJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@DataJpaTest
public class StudentServiceTest {

    @Autowired
    private StudentJpaRepository studentJpaRepository;

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

    @Autowired
    private EnrollmentJpaRepository enrollmentJpaRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final Logger LOGGER = LoggerFactory.getLogger(StudentService.class);

    @BeforeEach
    public void setData() {
        List<String> departmentNames = List.of("소프트웨어학부", "전기전자공학부", "경영학부", "경제학부", "기계공학부");

        List<Department> departments = departmentNames.stream().map(departmentName ->
                        Department.builder()
                        .name(departmentName)
                        .build()
                ).toList();

        departmentJpaRepository.saveAll(departments);

        List<Student> students = IntStream.range(0, 100)
                .mapToObj(i -> Student.builder()
                        .id(i+1)
                        .email("test"+i+"@gmail.com")
                        .password(passwordEncoder.encode("123123"))
                        .createdAt(LocalDateTime.now())
                        .enrollments(new HashSet<>())
                        .roles(List.of("ROLE_USER"))
                        .build()
                        ).toList();

        studentJpaRepository.saveAll(students);

        List<Enrollment> enrollments = students.stream().flatMap(student -> {
            List<Enrollment> enrollmentList = new ArrayList<>();
            Random random = new Random();

            for(int i = 0; i < random.nextInt(departments.size()); i++) {
                Enrollment enrollment = new Enrollment();
                enrollment.setDepartment(departments.get(i));
                enrollment.setStudent(student);
                enrollmentList.add(enrollment);
            }

            return enrollmentList.stream();
        }).toList();

        enrollmentJpaRepository.saveAll(enrollments);
    }

    @AfterEach
    public void clearData() {
        // 먼저, Enrollment 데이터 삭제
        enrollmentJpaRepository.deleteAll();

        // 다음으로, Student 데이터 삭제
        studentJpaRepository.deleteAll();

        // 마지막으로, Department 데이터 삭제
        departmentJpaRepository.deleteAll();
    }

    @Test
    public void fetchJoinTest() {
        LOGGER.info("-----------------패치 조인 테스트 시작-----------------");

        String email = "test0@gmail.com";

        List<Student> students = studentJpaRepository.findAll();

        students.forEach(student -> {
            LOGGER.info("[학생]: {}", student.getEnrollments());
        });

    }
}
