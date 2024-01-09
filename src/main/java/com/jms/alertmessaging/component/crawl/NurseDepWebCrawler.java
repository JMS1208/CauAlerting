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

    private final Logger LOGGER = LoggerFactory.getLogger(NurseDepWebCrawler.class);

    private static String BASE_URL = "://nursing.cau.ac.kr/pages//bm/bm_7_1_view.php?idx=";
    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        int postNumber = postNum;

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[간호학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            // 제목 추출
            Element titleElement = doc.select("td.subject h4").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 작성자 추출
            Element writerElement = doc.select("th:contains(작성자) + td").first();
            String writer = writerElement.text();

            // 날짜 추출
            Element dateElement = doc.select("td.date").first();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
            LocalDate postAt = LocalDate.parse(dateElement.text(), formatter);

            LOGGER.info("[간호학부] 제목: {}, 작성자: {}, 날짜: {}", title, writer, postAt);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(https + baseUrl)
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
