package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;
import jakarta.transaction.Transactional;
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
@Qualifier("machineEngDepCrawler")
public final class MachineEngDepCrawler implements WebCrawler {

    private final Logger LOGGER = LoggerFactory.getLogger(MachineEngDepCrawler.class);

    public static String[] BASE_URLS = {"://me.cau.ac.kr/bbs/board.php?bo_table=sub5_6&wr_id=", "://me.cau.ac.kr/bbs/board.php?bo_table=sub5_1&wr_id=", "://me.cau.ac.kr/bbs/board.php?bo_table=sub5_3&wr_id="};

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        int postNumber = postNum;

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[기계공학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            // 제목 추출
            Element titleElement = doc.select(".view-wrap h1").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.select(".view-wrap div[style*='float:right']").first();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(dateElement.text().split("\\s+")[0], formatter);

            LOGGER.info("[기계공학부] 제목: {}, 날짜: {}", title, postAt);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(https + baseUrl)
                    .writer(null)
                    .title(title)
                    .department(department)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }

        return boards;
    }

//    private List<Board> crawlItem(Department department, String baseUrl, Integer postNum) throws IOException {
//        int postNumber = postNum;
//
//        String http = "http";
//        String https = "https";
//
//        List<Board> boards = new ArrayList<>();
//
//        for(int i = 0; i < CRAWL_COUNT; i++) {
//            int newPostNum = postNumber + i;
//
//            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
//                    .get();
//
//            LOGGER.info("[기계공학부 크롤링 시도] : {}", http + baseUrl + newPostNum);
//
//            // 제목 추출
//            Element titleElement = doc.select(".view-wrap h1").first();
//
//            if(Objects.isNull(titleElement)) continue;
//
//            String title = titleElement.text();
//
//            if(title.isEmpty()) continue;
//
//            // 날짜 추출
//            Element dateElement = doc.select(".view-wrap div[style*='float:right']").first();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate postAt = LocalDate.parse(dateElement.text().split("\\s+")[0], formatter);
//
//            LOGGER.info("[기계공학부] 제목: {}, 날짜: {}", title, postAt);
//
//            Board board = Board.builder()
//                    .postNumber(newPostNum)
//                    .link(https + baseUrl)
//                    .writer(null)
//                    .title(title)
//                    .department(department)
//                    .postAt(postAt)
//                    .build();
//
//            boards.add(board);
//        }
//
//        return boards;
//    }
}
