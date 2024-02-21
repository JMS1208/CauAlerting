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
@Qualifier("chemicalEngDepCrawler")
public final class ChemicalEngDepCrawler implements WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChemicalEngDepCrawler.class);

    public static void main(String[] args) throws IOException {
        int postNumber = 256;

        String baseUrl = "://chemeng.cau.ac.kr/2018/sub05/sub05_01_view.php?bbsIdx=";

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[화학신소재공학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            Element titleElement = doc.selectFirst("table.board_view th");

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            Element dateElement = doc.selectFirst("table.board_view td.left");

            String date = dateElement.text().split("\\|")[1].trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            LOGGER.info("[화공 테스트] : {}, {}", title, postAt);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(https + baseUrl)
                    .writer(null)
                    .title(title)
                    .postAt(postAt)
                    .build();

            boards.add(board);
        }
    }

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {
        int postNumber = postNum;

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[화학신소재공학부 크롤링 시도] : {}", http + baseUrl + newPostNum);

            Element titleElement = doc.selectFirst("table.board_view th");

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            Element dateElement = doc.selectFirst("table.board_view td.left");

            if(Objects.isNull(dateElement)) continue;

            String date = dateElement.text().split("\\|")[1].trim();

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
