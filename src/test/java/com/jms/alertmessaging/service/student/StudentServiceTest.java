package com.jms.alertmessaging.service.student;

import com.jms.alertmessaging.dto.student.KeywordDto;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.keyword.Keyword;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.enrollment.EnrollmentJpaRepository;
import com.jms.alertmessaging.repository.keyword.KeywordJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.service.auth.AuthService;
import com.jms.alertmessaging.service.enrollment.EnrollmentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")

class StudentServiceTest {

    @Autowired
    StudentJpaRepository studentJpaRepository;

    @Autowired
    DepartmentJpaRepository departmentJpaRepository;

    @Autowired
    EnrollmentJpaRepository enrollmentJpaRepository;

    @Autowired
    KeywordJpaRepository keywordJpaRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    AuthService authService;

    @Autowired
    EnrollmentService enrollmentService;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    Logger LOGGER = LoggerFactory.getLogger(StudentService.class);

    void 학부_등록() {
        List<String> departmentNames = List.of("소프트웨어학부");

        List<Department> departments = departmentNames.stream().map(departmentName ->
                Department.builder()
                        .name(departmentName)
                        .build()
        ).toList();

        departmentJpaRepository.saveAll(departments);
    }

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
                        .id(i + 1)
                        .email("test" + i + "@gmail.com")
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

            for (int i = 0; i < random.nextInt(departments.size()); i++) {
                Enrollment enrollment = new Enrollment();
                enrollment.setDepartment(departments.get(i));
                enrollment.setStudent(student);
                enrollmentList.add(enrollment);
            }

            return enrollmentList.stream();
        }).toList();

        enrollmentJpaRepository.saveAll(enrollments);
    }

    //    @AfterEach
    public void clearData() {
        // 먼저, Enrollment 데이터 삭제
        enrollmentJpaRepository.deleteAll();

        // 다음으로, Student 데이터 삭제
        studentJpaRepository.deleteAll();

        // 마지막으로, Department 데이터 삭제
        departmentJpaRepository.deleteAll();
    }

    @BeforeEach
    void 초기화() {
        //학부 등록
        Department department = new Department(1L, "소프트웨어학부");
        departmentJpaRepository.save(department);

        //유저 생성하고
        Student student = Student.builder()
                .id(1L)
                .email("student@test.com")
                .password("123123")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        studentJpaRepository.save(student);

        Authentication authentication = new UsernamePasswordAuthenticationToken(student, null, student.getAuthorities());

        //시큐리티 컨텍스트에 등록 - 로그인
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //학부 등록
        enrollmentService.saveEnrollment(student, department);
    }

    @AfterEach
    public void 로그아웃() {
        //테스트 끝난 후, SecurityContext를 클리어
        SecurityContextHolder.clearContext();
    }


    @Test
    public void 패치_조인() {
        String email = "test0@gmail.com";

        List<Student> students = studentJpaRepository.findAll();

        students.forEach(student -> {
            LOGGER.info("[학생]: {}", student.getEnrollments());
        });

    }

    @Test
    public void 키워드_업데이트() throws Exception {
        //given
        Student student = authService.getCurrentUser();
        LOGGER.info("학생: {}", student.getEmail());

        List<String> contents = List.of("키워드1", "키워드2");
        KeywordDto keywordDto = new KeywordDto(1L, contents);
        studentService.updateKeyword(keywordDto);

        //when


        Set<Enrollment> enrollments = enrollmentJpaRepository.findByStudentId(student.getId());

        if(enrollments == null) {
            Assertions.fail();
        }

        //then
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getDepartment().getId() == keywordDto.getDepartmentId()) {
                Assertions.assertEquals(contents, enrollment.getKeywords().stream().map(Keyword::getContent).collect(Collectors.toList()));
            }
        }

    }


    @Transactional
    @Test
    public void 키워드_업데이트_중복처리() throws Exception {
        //given
        Student student = authService.getCurrentUser();
        LOGGER.info("학생: {}", student.getEmail());

        //when
        List<String> contents1 = List.of("키워드1", "키워드2");
        KeywordDto keywordDto1 = new KeywordDto(1L, contents1);
        studentService.updateKeyword(keywordDto1);

        Enrollment enrollment1 = enrollmentJpaRepository.findByStudentIdAndDepartmentId(student.getId(), 1L);

        List<Keyword> keywords = keywordJpaRepository.findAllByEnrollment_Id(enrollment1.getId()).stream().toList();

        LOGGER.info("테스트: {}", enrollment1.getKeywords().stream().map(Keyword::getContent).toList());

        LOGGER.info("등록1 : {}", keywords.stream().map(Keyword::getContent).toList());

//        List<String> contents2 = List.of("키워드1", "키워드3");
//        KeywordDto keywordDto2 = new KeywordDto(1L, contents2);
//        studentService.updateKeyword(keywordDto2);
//
//        //then
//        Enrollment enrollment2 = enrollmentJpaRepository.findByStudentIdAndDepartmentId(student.getId(), 1L);
//
//        LOGGER.info("등록2 : {}", enrollment2.getKeywords().stream().map(Keyword::getContent).toList());
//        Assertions.assertArrayEquals(contents2.toArray(), enrollment2.getKeywords().stream().map(Keyword::getContent).toArray());
    }


}
