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

//첨단 소재 공학과
@Service
@Qualifier("advancedMaterialDepCrawler")
public final class AdvancedMaterialDepCrawler implements WebCrawler {


    //첨소공도 https 연결이 안 되어있다.
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedMaterialDepCrawler.class);

    public static void main(String[] args) throws IOException {
        int postNumber = 290;

        String baseUrl = "://ame.cau.ac.kr/bbs/board.php?tbl=bbs45&mode=VIEW&num=";

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[첨소공 크롤링 시도] : {}", http + baseUrl + newPostNum);

            // 제목 추출
            Element titleElement = doc.select("p.title").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 글쓴이와 날짜 추출
            Element conElement = doc.select("p.con").first();

            if(Objects.isNull(conElement)) continue;

            String conText = conElement.text();

            if(conText.isEmpty()) continue;

            // 글쓴이와 날짜 분리
            String[] parts = conText.split("│");
            String writer = parts[0].trim();
            String date = parts[1].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            LOGGER.info("[테스트] : {}, {}", title, postAt);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(http + baseUrl)
                    .writer(writer)
                    .title(title)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }
    }

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {
        int postNumber = postNum;

        baseUrl = baseUrl.replace(http, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[첨소공 크롤링 시도] : {}", http + baseUrl + newPostNum);

            // 제목 추출
            Element titleElement = doc.select("p.title").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 글쓴이와 날짜 추출
            Element conElement = doc.select("p.con").first();

            if(Objects.isNull(conElement)) continue;

            String conText = conElement.text();

            if(conText.isEmpty()) continue;

            // 글쓴이와 날짜 분리
            String[] parts = conText.split("│");
            String writer = parts[0].trim();
            String date = parts[1].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(http + baseUrl)
                    .writer(writer)
                    .title(title)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }

        return boards;
    }
}
