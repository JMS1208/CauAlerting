package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Qualifier("archiDepCrawler")
public final class ArchiDepCrawler implements WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiDepCrawler.class);

    //크롤링 테스트
    public static void main(String[] args) throws IOException {
        Department department = Department.builder()
                .id(5)
                .name("건축학부")
                .build();

        String baseUrl = "://archicau.com/wordpress/공지사항/?mod=document&uid=";

        int postNumber = 1274;

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[건축학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            //제목 추출
            Element titleElement = doc.selectFirst("div.kboard-title h1");

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.selectFirst("div.detail-date .detail-value");

            String date = dateElement.text().split(" ")[0].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            Element writerElement = doc.selectFirst("div.detail-writer .detail-value");

            String writer = writerElement.text();

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(http + baseUrl)
                    .writer(writer)
                    .title(title)
                    .department(department)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }

        LOGGER.info("[결과] : {}", boards);
    }

    //현재 건축학부 사이트는 https 가 지원되지 않고 있다.
    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {
        int postNumber = postNum;

        baseUrl = baseUrl.replace(http, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[건축학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            //제목 추출
            Element titleElement = doc.selectFirst("div.kboard-title h1");

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.selectFirst("div.detail-date .detail-value");

            String date = dateElement.text().split(" ")[0].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            Element writerElement = doc.selectFirst("div.detail-writer .detail-value");

            String writer = writerElement.text();

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(http + baseUrl)
                    .writer(writer)
                    .title(title)
                    .department(department)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }

        return boards;
    }
}
