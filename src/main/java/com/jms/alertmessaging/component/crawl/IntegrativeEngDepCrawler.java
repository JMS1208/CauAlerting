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

//융합공학과
@Service
@Qualifier("integrativeEngDepCrawler")
public final class IntegrativeEngDepCrawler implements WebCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrativeEngDepCrawler.class);

    public static void main(String[] args) throws IOException {
        int postNumber = 1490;

        String baseUrl = "://ie.cau.ac.kr/sch_5/notice.php?p_mode=view&p_idx=";

        List<Board> boards = new ArrayList<>();

        for(int i = 0; i < CRAWL_COUNT; i++) {
            int newPostNum = postNumber + i;

            Document doc = Jsoup.connect(http + baseUrl + newPostNum)
                    .get();

            LOGGER.info("[융공 크롤링 시도] : {}", http + baseUrl + newPostNum);

            //제목 추출
            Element titleElement = doc.select("table.table-notice-detail h3").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.select("table.table-notice-detail p.txt-s").first();

            if(Objects.isNull(dateElement)) continue;

            String date = dateElement.text().split("작성일 : ")[1].split(" ")[0];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);


            LOGGER.info("[테스트] 가져온 것: {}, {}", title, postAt);

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

            LOGGER.info("[융공 크롤링 시도] : {}", http + baseUrl + newPostNum);

            //제목 추출
            Element titleElement = doc.select("table.table-notice-detail h3").first();

            if(Objects.isNull(titleElement)) continue;

            String title = titleElement.text();

            if(title.isEmpty()) continue;

            // 날짜 추출
            Element dateElement = doc.select("table.table-notice-detail p.txt-s").first();

            if(Objects.isNull(dateElement)) continue;

            String date = dateElement.text().split("작성일 : ")[1].split(" ")[0];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate postAt = LocalDate.parse(date, formatter);

            Board board = Board.builder()
                    .postNumber(newPostNum)
                    .link(https + baseUrl)
                    .writer(null)
                    .title(title)
                    .postAt(postAt)
                    .department(department)
                    .build();

            boards.add(board);
        }

        return boards;
    }
}
