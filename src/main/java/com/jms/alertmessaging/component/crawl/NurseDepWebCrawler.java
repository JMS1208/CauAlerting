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
@Qualifier("nurseDepCrawler")
public final class NurseDepWebCrawler implements WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NurseDepWebCrawler.class);

    @Override
    public List<Board> crawlFrom(Department department, Integer postNum) throws IOException {

        int postNumber = Objects.isNull(postNum) ? NURSE_ADMIN_INIT_POST_NUM : postNum;

        String http = "http";
        String https = "https";

        String baseUrl = "://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=";

        List<Board> boards = new ArrayList<>();

        while(true) {
            Document doc = Jsoup.connect(http + baseUrl + postNumber)
                    .get();

            // 제목 추출
            Element titleElement = doc.select("td.subject h4").first();

            if(Objects.isNull(titleElement)) break;

            String title = titleElement.text();

            if(title.isEmpty()) break;

            // 작성자 추출
            Element writerElement = doc.select("th:contains(작성자) + td").first();
            String writer = writerElement.text();

            // 날짜 추출
            Element dateElement = doc.select("td.date").first();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
            LocalDate postAt = LocalDate.parse(dateElement.text(), formatter);

            LOGGER.info("[간호학부] 제목: {}, 작성자: {}, 날짜: {}", title, writer, postAt);

            Board board = Board.builder()
                    .postNumber(postNumber)
                    .link(https + baseUrl + postNumber)
                    .writer(writer)
                    .title(title)
                    .department(department)
                    .postAt(postAt)
                    .build();

            boards.add(board);

            postNumber++;
        }

        return boards;
    }

}
