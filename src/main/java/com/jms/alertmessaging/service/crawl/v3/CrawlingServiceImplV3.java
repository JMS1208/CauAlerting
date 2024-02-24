package com.jms.alertmessaging.service.crawl.v3;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.board.QBoard;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.repository.post.BoardJpaRepository;
import com.jms.alertmessaging.service.crawl.v1.CrawlingService;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlingServiceImplV3 implements CrawlingServiceV3 {
    
    private final BoardJpaRepository boardJpaRepository;
    private final CrawlingService crawlingService;
    private final EmailSender emailSender;
    private final QBoard qBoard = QBoard.board;
    private final QBoard qBoardSub = new QBoard("board");

    private final JPAQueryFactory queryFactory;

    private final List<CrawlingListener> crawlingListeners = new ArrayList<>();

    @Override
    public void addCrawlingListener(CrawlingListener listener) {
        if(!crawlingListeners.contains(listener)) {
            crawlingListeners.add(listener);
        }
    }

    @Override
    public void removeCrawlingListener(CrawlingListener listener) {
        crawlingListeners.remove(listener);
    }

    @Override
    public void crawl() {

        if(!isWithInRange()) {
            return;
        }

        //마지막에 크롤링한 것들
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

        for (Board recentBoard : recentBoards) {

            try {
                Department department = recentBoard.getDepartment();
                String baseUrl = recentBoard.getLink();
                Integer postNum = recentBoard.getPostNumber() + 1;

                //크롤링 전
                crawlingListeners.forEach(listener -> listener.beforeCrawl(department));

                //새로 크롤링해 온 게시글들
                List<Board> crawledBoards = crawlingService.crawlFrom(department, baseUrl, postNum);

                if (crawledBoards.isEmpty()) continue;

                //크롤링 후
                crawlingListeners.forEach(listener -> listener.afterCrawl(department, crawledBoards));

                //디비 저장 전
                crawlingListeners.forEach(listener -> listener.beforeSave(department, crawledBoards));

                //새로 크롤링한 것 디비에 저장
                boardJpaRepository.saveAll(crawledBoards);

                //디비 저장 후
                crawlingListeners.forEach(listener -> listener.afterSave(department, crawledBoards));

            }  catch (SocketTimeoutException | ConnectException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
                emailSender.sendEmailToPerson("wjsalstjr59@gmail.com", "중앙대 알림이 오류 발생", recentBoard.getDepartment().getName()+": " + e);
            }

        }

    }

    public boolean isWithInRange() {
        // 한국 시간대로 현재 시간 설정
        ZonedDateTime nowInKorea = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        // 오전 8시와 오후 9시를 크롤링 시간으로 설정
        LocalTime start = LocalTime.of(START_HOUR, 0);
        LocalTime end = LocalTime.of(END_HOUR, 0);

        // 현재 시간이 8시부터 21시 사이인지 확인
        return nowInKorea.toLocalTime().isAfter(start) && nowInKorea.toLocalTime().isBefore(end);
    }

}
