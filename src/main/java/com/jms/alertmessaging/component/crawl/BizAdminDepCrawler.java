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

    private static final Logger LOGGER = LoggerFactory.getLogger(BizAdminDepCrawler.class);

    // Department 에 대해서, postNumber 부터 확인
    @Override
    public List<Board> crawlFrom(Department department, Integer postNum) throws IOException {

        int postNumber = Objects.isNull(postNum) ? BIZ_ADMIN_INIT_POST_NUM : postNum;

        String http = "http";

        String https = "https";

        String baseUrl = "://biz.cau.ac.kr/2016/sub06/sub06_01_view.php?bbsIdx=";

        List<Board> boards = new ArrayList<>();

        while(true) {
            Document doc = Jsoup.connect(http + baseUrl + postNumber)
                    .get();

            //제목 추출
            Element titleElement = doc.selectFirst(".txt");

            String title = titleElement.text();

            if(title.isEmpty()) break;

            // 날짜 추출
            Element dateElement = doc.selectFirst(".date");

            String date = dateElement.text().split("\\|")[0].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            Board board = Board.builder()
                    .postNumber(postNumber)
                    .link(https + baseUrl + postNumber)
                    .writer(null)
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
