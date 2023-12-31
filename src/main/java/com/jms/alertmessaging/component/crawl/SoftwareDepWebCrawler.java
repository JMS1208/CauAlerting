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
@Qualifier("softwareDepCrawler")
public final class SoftwareDepWebCrawler implements WebCrawler {

    public final Logger LOGGER = LoggerFactory.getLogger(SoftwareDepWebCrawler.class);

    public static String BASE_URL =  "://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=";

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        int postNumber = postNum;

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[소프트 크롤링 시도] : {}", http + baseUrl + newPostNum);

            Element titleElement = doc.select("div.header > h3").first();
            Element dateElement = doc.select("div.header span:nth-child(1)").first();
            Element authorElement = doc.select("div.header > div > span:nth-child(3)").first();

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            String writer = authorElement.text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(dateElement.text(), formatter);

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
