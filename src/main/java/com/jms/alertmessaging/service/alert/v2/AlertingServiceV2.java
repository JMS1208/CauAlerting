package com.jms.alertmessaging.service.alert.v2;

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
import com.jms.alertmessaging.service.alert.AlertingService;
import com.jms.alertmessaging.service.crawl.v1.CrawlingService;
import com.jms.alertmessaging.service.crawl.v3.CrawlingListener;
import com.jms.alertmessaging.service.crawl.v3.CrawlingServiceV3;
import com.jms.alertmessaging.service.mail.EmailSender;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class AlertingServiceV2 implements AlertingService {

    private final QDepartment qDepartment = QDepartment.department;
    private final QEnrollment qEnrollment = QEnrollment.enrollment;
    private final QStudent qStudent = QStudent.student;
    private final EmailSender emailSender;

    private final JPAQueryFactory queryFactory;

    private final CrawlingServiceV3 crawlingService;


    private final CrawlingListener crawlingListener = new CrawlingListener() {
        @Override
        public void beforeCrawl(Department department) {

        }

        @Override
        public void afterCrawl(Department department, List<Board> boards) {

        }

        @Override
        public void beforeSave(Department department, List<Board> boards) {

        }

        @Override
        public void afterSave(Department department, List<Board> boards) {
            //이메일 보내기

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
            List<String> emailAddresses = students.stream().map(Student::getEmail).toList();

            String title = toTitle(department);

            String content = toContent(department, boards);

            emailSender.sendEmailToPeople(emailAddresses, title, content);

        }
    };

    //주기적으로 크롤링해서 이메일 보내고 디비에 저장하기
    @Scheduled(fixedRate = 120_000)
    @Transactional
    public void crawlingAndSendEmail() {

        crawlingService.addCrawlingListener(crawlingListener);

        crawlingService.crawl();
    }

    private String toTitle(Department department) {
        return department.getName() + " 새글 알림";
    }

    private String toContent(Department department, List<Board> boards) {
        StringBuilder sb = new StringBuilder();

        //url로 하나의 게시글에 접근이 불가능한 경우
        //공공인재학부 - 9

        List<Long> exclude = List.of(9L);

        for (Board board : boards) {
            sb.append("[ ").append(board.title).append("] \n");
            sb.append("(").append(board.link);
            if (!exclude.contains(department.getId())) {
                sb.append(board.postNumber);
            }
            sb.append(") \n\n\n");
        }

        return sb.toString();
    }
}