package com.jms.alertmessaging.service.alert.v1;

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
import com.jms.alertmessaging.service.crawl.v1.CrawlingService;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class AlertingServiceV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertingServiceV1.class);

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
    @Scheduled(fixedRate = 120_000)
    @Transactional
    public void crawlingAndSendEmail() {

        // 한국 시간대로 현재 시간 설정
        ZonedDateTime nowInKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        // 오전 8시와 밤 11시를 LocalTime으로 설정
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(23, 0);

        // 현재 시간이 8시부터 23시 사이인지 확인
        boolean isWithinRange = nowInKorea.toLocalTime().isAfter(start) && nowInKorea.toLocalTime().isBefore(end);

        //오전 8시 ~ 오후 11시까지만 크롤링
        if (!isWithinRange) {
            return;
        }

        //학부별로 최근에 크롤링한 게시글
        List<Board> recentBoards = queryFactory
                .selectFrom(qBoard)
                .leftJoin(qBoard.department).fetchJoin() // 페치 조인 사용
                .where(qBoard.postNumber.in(
                        JPAExpressions
                                .select(qBoardSub.postNumber.max())
                                .from(qBoardSub)
                                .groupBy(qBoardSub.department, qBoardSub.link) // 학부와 링크 별로 그룹화
                                .having(qBoardSub.department.id.eq(qBoard.department.id)
                                        .and(qBoardSub.link.eq(qBoard.link))) // 학부와 링크가 메인 쿼리의 해당 값과 일치하는 경우
                ))
                .fetch();

        LOGGER.info("[최근 크롤링한 학부 수] : {}", recentBoards.size());

        for (Board recentBoard : recentBoards) {

            try {
                Department department = recentBoard.getDepartment();
                String baseUrl = recentBoard.getLink();
                Integer postNum = recentBoard.getPostNumber() + 1;

                //새로 크롤링해 온 게시글들
                List<Board> crawledBoards = crawlingService.crawlFrom(department, baseUrl, postNum);

                LOGGER.info("[크롤링 새로해 온 것] 학부: {}, 개수: {}", department.getName(), crawledBoards.size());

                if (crawledBoards.isEmpty()) continue;

                //새로 크롤링한 것 디비에 저장
                boardJpaRepository.saveAll(crawledBoards);

                //보낼 사람들
                List<Student> students = queryFactory
                        .selectFrom(qStudent)
                        .leftJoin(qStudent.roles)
                        .join(qStudent.enrollments, qEnrollment)
                        .join(qEnrollment.department, qDepartment)
                        .where(qDepartment.id.eq(department.getId()))
                        .distinct()
                        .fetch();

                //보낼 이메일들
                List<String> sendToEmails = students.stream().map(Student::getEmail).toList();

                LOGGER.info("[보낼 이메일들] {}", sendToEmails);

                if (sendToEmails.isEmpty()) continue;

                //이메일 보내기
                emailSender.sendEmailToPeople(sendToEmails, department.getName() + " 새글 알림", toContent(crawledBoards));

            } catch (SocketTimeoutException | ConnectException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
                emailSender.sendEmailToPerson("wjsalstjr59@gmail.com", "중앙대 알림이 오류 발생", recentBoard.getDepartment().getName() + ": " + e);
            }

        }

    }

    private String toContent(List<Board> boards) {
        StringBuilder sb = new StringBuilder();

        //url로 하나의 게시글에 접근이 불가능한 경우
        //공공인재학부 - 9

        List<Integer> exclude = List.of(9);

        for (Board board : boards) {
            sb.append("[ ").append(board.title).append("] \n");
            sb.append("(").append(board.link);
            if (!exclude.contains(board.postNumber)) {
                sb.append(board.postNumber);
            }
            sb.append(") \n\n\n");
        }

        return sb.toString();
    }
}