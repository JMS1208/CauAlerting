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
@Qualifier("bizAdminDepCrawler")
public final class BizAdminDepCrawler implements WebCrawler {

    private final Logger LOGGER = LoggerFactory.getLogger(BizAdminDepCrawler.class);

    private static String BASE_URL = "://biz.cau.ac.kr/2016/sub06/sub06_01_view.php?bbsIdx=";
    // Department 에 대해서, postNumber 부터 확인
    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        int postNumber = postNum;

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[경영 크롤링 시도] : {}", http + baseUrl + newPostNum);

            //제목 추출
            Element titleElement = doc.selectFirst(".txt");

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.selectFirst(".date");

            String date = dateElement.text().split("\\|")[0].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

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
}
