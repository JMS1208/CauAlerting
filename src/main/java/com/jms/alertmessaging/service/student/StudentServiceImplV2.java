package com.jms.alertmessaging.service.student;

import com.jms.alertmessaging.dto.student.DepartmentKeywords;
import com.jms.alertmessaging.dto.student.KeywordDto;
import com.jms.alertmessaging.dto.student.StudentInfoBundle;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.department.QDepartment;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.enrollment.QEnrollment;
import com.jms.alertmessaging.entity.keyword.Keyword;
import com.jms.alertmessaging.entity.keyword.QKeyword;
import com.jms.alertmessaging.entity.student.Frequency;
import com.jms.alertmessaging.entity.student.QStudent;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.exception.auth.InvalidAuthenticationException;
import com.jms.alertmessaging.exception.user.UserNotFoundException;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.enrollment.EnrollmentJpaRepository;
import com.jms.alertmessaging.repository.keyword.KeywordJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.service.auth.AuthService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Primary
@Service
@RequiredArgsConstructor
public class StudentServiceImplV2 implements StudentServiceV2 {

    private final StudentJpaRepository studentJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final KeywordJpaRepository keywordJpaRepository;
    private final JPAQueryFactory queryFactory;

    private final EnrollmentJpaRepository enrollmentJpaRepository;

    private final AuthService authService;

    private final QStudent qStudent = QStudent.student;
    private final QDepartment qDepartment = QDepartment.department;
    private final QEnrollment qEnrollment = QEnrollment.enrollment;
    private final QKeyword qKeyword = QKeyword.keyword;

    @Override
    public void deleteUserById(Long userId) {
        //UserMinors 추가시 테이블에서도 삭제
        studentJpaRepository.deleteById(userId);

    }

    @Override
    public Student findUserByEmail(String email) {
        return studentJpaRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다"));
    }


    @Override
    public StudentInfoBundle findUserBundleByEmail() throws NoSuchElementException, AuthenticationException {

        Authentication authentication = authService.getCurrentUserAuthentication();

        String email = authentication.getName();

        log.info("[findUserBundleByEmail] 이메일은 {}", email);

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.ASC, qDepartment.id);

        // 1. 이메일 2. 등록한 학부 3. 등록한 학부들 중 키워드를 등록한 것
        List<Tuple> results = queryFactory
                .select(
                    qKeyword.id, qKeyword.content, qDepartment.id, qDepartment.name
                )
                .from(qStudent)
                .leftJoin(qStudent.enrollments, qEnrollment)
                .leftJoin(qEnrollment.keywords, qKeyword)
                .leftJoin(qEnrollment.department, qDepartment)
                .where(qStudent.email.eq(email))
                .orderBy(orderSpecifier)
                .fetch();

        // 서브쿼리 정의

        List<Department> departments = departmentJpaRepository.findAll();

        //없는 경우
        if (results.isEmpty()) throw new InvalidAuthenticationException("유효하지 않은 사용자입니다.");

        Map<Long, DepartmentKeywords> departmentKeywordsMap = new TreeMap<>();

        for (Tuple result : results) {
            log.info("[findUserBundleByEmail] 결과 {}", result);
            Long departmentId = result.get(qDepartment.id);
            String departmentName = result.get(qDepartment.name);
            String keyword = result.get(qKeyword.content);

            if(Objects.isNull(departmentId)) continue;

            DepartmentKeywords departmentKeywords
                    = departmentKeywordsMap.getOrDefault(
                    departmentId, new DepartmentKeywords(
                            departmentId, departmentName, new ArrayList<>(), true));

            if (keyword != null) {
                departmentKeywords.getKeywords().add(keyword);
            }

            departmentKeywordsMap.put(departmentId, departmentKeywords);
        }

        List<DepartmentKeywords> departmentKeywordsList = new ArrayList<>(departmentKeywordsMap.values().stream().toList());



        for (Department department : departments) {
            long departmentId = department.getId();
            String departmentName = department.getName();
            if (!departmentKeywordsMap.containsKey(departmentId)) {
                departmentKeywordsList.add(
                        new DepartmentKeywords(
                                departmentId,
                                departmentName,
                                new ArrayList<>(),
                                false
                        )
                );
            }
        }

        departmentKeywordsList = departmentKeywordsList.stream().sorted(Comparator.comparingLong(DepartmentKeywords::getDepartmentId)).toList();

        return StudentInfoBundle.builder()
                .email(email)
                .departmentKeywordsList(departmentKeywordsList)
                .build();
    }


    @Override
    public void updateMyDepartment(long departmentId, boolean select) {
        Authentication authentication = authService.getCurrentUserAuthentication();

        String email = authentication.getName();

        // 학생 엔티티 조회
        Student student = studentJpaRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidAuthenticationException("Student not found"));

        Set<Enrollment> enrollments = enrollmentJpaRepository.findByStudentId(student.getId());

        log.info("[updateMyDepartment]: {}", enrollments);

        if (select) {
            Department department = departmentJpaRepository.findById(departmentId);
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setDepartment(department);

            enrollmentJpaRepository.save(enrollment);
        } else {

            for (Enrollment enrollment : enrollments) {
                if (enrollment.getDepartment().getId() == departmentId) {
                    //enrollment.getKeywords() 로는 바로 조회가 안되고 있음. 그래서 아래와 같이 직접 삭제 후 enrollment 삭
                    keywordJpaRepository.deleteAllByEnrollment_Id(enrollment.getId());
                    enrollmentJpaRepository.delete(enrollment);
                }
            }

        }

    }

    /**
     * 중복된 키워드 저장 X
     * 키워드 최대 10개
     * 키워드 글자 제한 10자
     * 학부마다 설정
     * 미등록시 모두 공지
     *
     * 기존꺼 삭제 -> 추가하기
     * */
    @Override
    public List<String> updateKeyword(KeywordDto keywordDto) {
        // 현재 로그인된 학생 가져와서
        Student student = authService.getCurrentUser();

        // 키워드를 등록할 학부 아이디
        Long departmentId = keywordDto.getDepartmentId();

        // 학생이 등록한 Enrollment 엔티티 가져옴
        Enrollment enrollment = enrollmentJpaRepository.findByStudentIdAndDepartmentId(student.getId(), departmentId);

        // 현재 키워드
        Set<Keyword> keywords = enrollment.getKeywords();

        //업데이트할 키워드 내용들
        Set<String> updateKeywordContents = new HashSet<>(keywordDto.trimKeywords());

        //삭제할 키워드들 - 현재 키워드들 중에 업데이트할 키워드 내용에 없는게 있다면, 삭제
        Set<Keyword> removeKeywords = keywords.stream()
                .filter(k -> !updateKeywordContents.contains(k.getContent()))
                .collect(Collectors.toSet());

        //현재 키워드 내용들
        Set<String> curKeywordContents = keywords.stream()
                .map(Keyword::getContent)
                .collect(Collectors.toSet());

        //추가할 키워드들 - 업데이트할 키워드 내용들 중에 현재 키워드에 없으면, 추가
        Set<Keyword> newKeywords = updateKeywordContents.stream()
                .filter(k -> !curKeywordContents.contains(k))
                .map(content -> {
                    Keyword keyword = new Keyword();
                    keyword.setContent(content);
                    keyword.setEnrollment(enrollment);
                    return keyword;
                })
                .collect(Collectors.toSet());

        //삭제할 키워드들 삭제
        enrollment.removeKeywords(removeKeywords);
        keywordJpaRepository.deleteAll(removeKeywords);

        //추가할 키워드들 추가
        enrollment.addKeywords(newKeywords);

        return new ArrayList<>(updateKeywordContents);
    }

    @Override
    public String updateFrequency(Frequency frequency){
        Student student = authService.getCurrentUser();
        student.setFrequency(frequency);
        studentJpaRepository.save(student);
        return student.getFrequency().getValue();
    }
}
