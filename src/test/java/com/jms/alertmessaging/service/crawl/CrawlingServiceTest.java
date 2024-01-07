package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.config.TestConfig;
import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.board.QBoard;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.department.QDepartment;
import com.jms.alertmessaging.entity.enrollment.QEnrollment;
import com.jms.alertmessaging.entity.keyword.QKeyword;
import com.jms.alertmessaging.entity.student.QStudent;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.post.BoardJpaRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)// 'test' 프로파일을 사용하여 테스트 환경 구성
public class CrawlingServiceTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    private Logger LOGGER = LoggerFactory.getLogger(CrawlingServiceTest.class);

    private QEnrollment qEnrollment = QEnrollment.enrollment;
    private QStudent qStudent = QStudent.student;
    private QBoard qBoard = QBoard.board;
    private QKeyword qKeyword = QKeyword.keyword;
    private QDepartment qDepartment = QDepartment.department;


    @Test
    public void getDepartmentsList() {
        List<Department> departments = departmentJpaRepository.findAll();

        for(Department department: departments) {
            LOGGER.info("테스트: {}", department.getName());
        }

//        for(int i = 0; i < 3; i++) {
//            Board board = Board.builder()
//                    .postAt(LocalDate.now())
//                    .title("제목 " + i + 1 )
//                    .writer("작성자 " + i + 1)
//                    .link("링크 예시")
//                    .postNumber(i+1)
//                    .department(departments.get(i))
//                    .build();
//
//            boardJpaRepository.save(board);
//        }

    }

    @Test
    public void getRecentBoardsTest() {

        QBoard qBoardSub = new QBoard("board");

        List<Board> boards = queryFactory
                .selectFrom(qBoard)
                .leftJoin(qBoard.department, qDepartment).fetchJoin()
                .where(qBoard.postNumber.in(
                        JPAExpressions
                                .select(qBoardSub.postNumber.max())
                                .from(qBoardSub)
                                .groupBy(qBoardSub.department.id)
                                .having(qBoardSub.department.id.eq(qBoard.department.id))
                ))
                .fetch();

        LOGGER.info("[테스트] 개수: {}", boards.size());

        for(Board board: boards) {
            LOGGER.info("[테스트] 학부 이름: {}, 포스트 번호: {}", board.getDepartment().getName(), board.postNumber);
        }



    }

    //특정 학부에 등록한 학생 가져오기
    @Test
    public void getStudentForDepartment() {
//        List<Student> students = queryFactory
//                .selectFrom(qStudent)
//                .leftJoin(qStudent.enrollments, qEnrollment).fetchJoin()
//                .leftJoin(qEnrollment.department, qDepartment).fetchJoin()
//                .where(qDepartment.id.eq(1L))
//                .fetch();
//
        List<Student> students = queryFactory
                .selectFrom(qStudent)
                .join(qStudent.enrollments, qEnrollment)
                .join(qEnrollment.department, qDepartment)
                .where(qDepartment.id.eq(1L))
                .distinct()
                .fetch();

        for(Student student: students) {

            LOGGER.info("[학생] 아이디: {}, 이메일: {}", student.getId(), student.getEmail());

        }
    }
}
