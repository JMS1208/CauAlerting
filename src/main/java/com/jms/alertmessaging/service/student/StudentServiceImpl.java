package com.jms.alertmessaging.service.student;

import com.jms.alertmessaging.dto.student.DepartmentKeywordsDto;
import com.jms.alertmessaging.dto.student.StudentInfoBundleDto;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.department.QDepartment;
import com.jms.alertmessaging.entity.enrollment.QEnrollment;
import com.jms.alertmessaging.entity.enrollment.keyword.QEnrollmentKeyword;
import com.jms.alertmessaging.entity.student.QStudent;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.department.DepartmentRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.repository.student.StudentRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentJpaRepository studentJpaRepository;
    private final DepartmentRepository departmentRepository;
    private final JPAQueryFactory queryFactory;


    private final QStudent qStudent = QStudent.student;
    private final QDepartment qDepartment = QDepartment.department;
    private final QEnrollment qEnrollment = QEnrollment.enrollment;
    private final QEnrollmentKeyword qEnrollmentKeyword = QEnrollmentKeyword.enrollmentKeyword;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, StudentJpaRepository studentJpaRepository, DepartmentRepository departmentRepository, JPAQueryFactory queryFactory) {
        this.studentRepository = studentRepository;
        this.studentJpaRepository = studentJpaRepository;
        this.departmentRepository = departmentRepository;
        this.queryFactory = queryFactory;
    }

    //Minor 추가
    @Override
    public void addMinorDepartment(Long userId, Long departmentId) {
        Student student = studentRepository.findUserById(userId);
        Department department = departmentRepository.findDepartmentById(departmentId).orElseThrow();

//        userEntity.addMinorDepartment(departmentEntity);
        studentRepository.saveUser(student);
    }

    //Minor 삭제
    @Override
    public void removeMinorDepartment(Long userId, Long departmentId) {
        Student student = studentRepository.findUserById(userId);
        Department department = departmentRepository.findDepartmentById(departmentId).orElseThrow();

//        userEntity.removeMinorDepartment(departmentEntity);
        studentRepository.saveUser(student);
    }

    @Override
    public void deleteUserById(Long userId) {
        //UserMinors 추가시 테이블에서도 삭제
        studentJpaRepository.deleteById(userId);

    }

    @Override
    public Student findUserByEmail(String email) {
        Student student = studentRepository.findUserByEmail(email);
        return student;
    }


    @Override
    public StudentInfoBundleDto findUserBundleByEmail(String email) throws NoSuchElementException {

        //가져와야하는 것
        // 1. 이메일 2. 등록한 학부 3. 등록한 학부들 중 키워드를 등록한 것
        List<Tuple> results = queryFactory
                .select(qStudent.email, qEnrollment.department.id, qDepartment.name, qEnrollmentKeyword.keyword)
                .from(qStudent)
                .join(qStudent.enrollments, qEnrollment)
                .join(qEnrollment.department, qDepartment)
                .join(qEnrollment.keywords, qEnrollmentKeyword)
                .where(qStudent.email.eq(email))
                .fetch();

        //없는 경우
        if (results.isEmpty()) throw new NoSuchElementException();

        String userEmail = getOrThrow(results.get(0), qStudent.email);
        Map<Long, DepartmentKeywordsDto> departmentKeywordsMap = new HashMap<>();

        for (Tuple result : results) {
            long departmentId = getOrThrow(result, qDepartment.id);
            String departmentName = getOrThrow(result, qDepartment.name);
            String keyword = getOrThrow(result, qEnrollmentKeyword.keyword);

            departmentKeywordsMap
                    .computeIfAbsent(departmentId, k -> DepartmentKeywordsDto.builder()
                            .department(departmentName)
                            .keywords(new HashSet<>())
                            .build()
                    )
                    .getKeywords().add(keyword);
        }

        return StudentInfoBundleDto.builder()
                .email(userEmail)
                .departmentKeywords(departmentKeywordsMap)
                .build();
    }

    //null일떄 예외 날리기
    private <T> T getOrThrow(Tuple result, Expression<T> expression) {
        return Optional.ofNullable(result.get(expression)).orElseThrow();
    }
}
