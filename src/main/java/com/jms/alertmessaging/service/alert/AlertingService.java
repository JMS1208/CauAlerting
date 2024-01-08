package com.jms.alertmessaging.service.alert;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.board.QBoard;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.department.QDepartment;
import com.jms.alertmessaging.entity.enrollment.QEnrollment;
import com.jms.alertmessaging.entity.student.QStudent;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.post.BoardJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.jms.alertmessaging.service.crawl.CrawlingService;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlertingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertingService.class);

    private final BoardJpaRepository boardJpaRepository;

    private final StudentJpaRepository studentJpaRepository;

    private final DepartmentJpaRepository departmentJpaRepository;

    private final CrawlingService crawlingService;
    private final QDepartment qDepartment = QDepartment.department;
    private final QEnrollment qEnrollment = QEnrollment.enrollment;
    private final QBoard qBoard = QBoard.board;
    private final QStudent qStudent = QStudent.student;
    private final EmailSender emailSender;

    private final QBoard qBoardSub = new QBoard("board");

    private final JPAQueryFactory queryFactory;

    //주기적으로 크롤링해서 이메일 보내고 디비에 저장하기
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void crawlingAndSendEmail() {

        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(8,0);
        LocalTime end = LocalTime.of(23,0);

        //오전 8시 ~ 오후 11시까지만 크롤링
        if(now.isBefore(start) || now.isAfter(end)) {
            return;
        }

        try {
            //학부별로 최근에 크롤링한 게시글

            List<Board> recentBoards = queryFactory
                    .selectFrom(qBoard)
                    .leftJoin(qBoard.department).fetchJoin() // 페치 조인 사용
                    .where(qBoard.postNumber.in(
                            JPAExpressions
                                    .select(qBoardSub.postNumber.max())
                                    .from(qBoardSub)
                                    .groupBy(qBoardSub.department)
                                    .having(qBoardSub.department.id.eq(qBoard.department.id))
                    ))
                    .fetch();

            LOGGER.info("[최근 크롤링한 학부 수] : {}", recentBoards.size());

            //모든 학부
            List<Department> departments = departmentJpaRepository.findAll();


            //학부별로 최근에 크롤링한 게시글
            Map<Long, Board> recentBoardsMap = new HashMap<>();

            for(Board recentBoard: recentBoards) {
                recentBoardsMap.put(recentBoard.getDepartment().getId(), recentBoard);
            }

            //모든 학부를 크롤링할 건데, 최근에 크롤링한 게시글이 있는 것과 없는 것 구별해서
            for(Department department: departments) {

                LOGGER.info("[학부] {}", department.getName());
                //이 학부에 대해서 최근에 크롤링한 게시글이 없다면 null
                Integer postNum = null;

                //이 학부에 대해서 최근에 크롤링한 게시글이 있다면
                if(recentBoardsMap.containsKey(department.getId())) {
                    LOGGER.info("[{}] 최근 크롤링한 것 있음", department.getName());
                    postNum = recentBoardsMap.get(department.getId()).postNumber+1;
                }

                LOGGER.info("[포스트 넘버] {}", postNum);

                //새로 크롤링해 온 게시글들
                List<Board> crawledBoards = crawlingService.crawlFrom(department, postNum);

                LOGGER.info("[크롤링 새로해 온 것] 학부: {}, 개수: {}", department.getName(), crawledBoards.size());

                if(crawledBoards.isEmpty()) continue;

                //새로 크롤링한 것 디비에 저장
                boardJpaRepository.saveAll(crawledBoards);

                //보낼 사람들
                List<Student> students = queryFactory
                        .selectFrom(qStudent)
                        .join(qStudent.enrollments, qEnrollment)
                        .join(qEnrollment.department, qDepartment)
                        .where(qDepartment.id.eq(department.getId()))
                        .distinct()
                        .fetch();

                //보낼 이메일들
                List<String> sendToEmails = students.stream().map(Student::getEmail).toList();

                LOGGER.info("[보낼 이메일들] {}", sendToEmails);

                if(sendToEmails.isEmpty()) continue;

                //이메일 보내기
                emailSender.sendEmailToPeople(sendToEmails, department.getName() +" 새글 알림", toContent(crawledBoards));

            }

        } catch (Exception e) {
            e.printStackTrace();
            emailSender.sendEmailToPerson("wjsalstjr59@gmail.com", "중앙대 알림이 오류 발생", e.toString());
        }
    }

    private String toContent(List<Board> boards) {
        StringBuilder sb = new StringBuilder();

        for(Board board: boards) {
            sb.append("[ ").append(board.title).append("] \n");
            sb.append("(").append(board.link).append(") \n\n\n");
        }

        return sb.toString();
    }
}