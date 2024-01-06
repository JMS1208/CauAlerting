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

    public static final Logger LOGGER = LoggerFactory.getLogger(SoftwareDepWebCrawler.class);

    @Override
    public List<Board> crawlFrom(Department department, Integer postNum) throws IOException {

        int postNumber = Objects.isNull(postNum) ? softInitPostNumber : postNum;

        String http = "http";
        String https = "https";

        String baseUrl = "://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=";

        List<Board> boards = new ArrayList<>();

        while(true) {
            Document doc = Jsoup.connect(http + baseUrl + postNumber)
                    .get();

            Element titleElement = doc.select("div.header > h3").first();
            Element dateElement = doc.select("div.header span:nth-child(1)").first();
            Element authorElement = doc.select("div.header > div > span:nth-child(3)").first();

            String title = titleElement.text();

            if(title.isEmpty()) break;

            String writer = authorElement.text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(dateElement.text(), formatter);

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
