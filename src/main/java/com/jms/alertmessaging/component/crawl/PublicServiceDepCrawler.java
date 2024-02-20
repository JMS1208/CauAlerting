package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
@Qualifier("publicServiceDepCrawler")
public final class PublicServiceDepCrawler implements WebCrawler {


    private static final Logger LOGGER = LoggerFactory.getLogger(PublicServiceDepCrawler.class);

    public static void main(String[] args) throws IOException {

        String baseUrl = "https://public.cau.ac.kr/04_pov/pov_01a.php";

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        Document doc = Jsoup.connect(http + baseUrl)
                .get();

        // 'line_bottom' 클래스를 가진 td 태그를 포함하는 tr 태그들만 선택
        Elements rows = doc.select("tr:has(td.line_bottom)");

        int count = 0;

        for (Element row : rows) {
            Elements tds = row.select("td.line_bottom");

            // 행이 비어있지 않고 첫 번째 'td'가 숫자로 시작하는 경우만 처리
            if (!tds.isEmpty() && tds.get(0).text().matches("\\d+")) {
                String numberStr = tds.get(0).text();
                int postNumber = Integer.parseInt(numberStr);
                String title = tds.get(1).text().trim();
                String date = tds.get(2).text();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate postAt = LocalDate.parse(date, formatter);

                if(postNumber > 982) {
                    Board board = Board.builder()
                            .postNumber(postNumber)
                            .link(https + baseUrl)
                            .writer(null)
                            .title(title)
                            .department(null)
                            .postAt(postAt)
                            .build();

                    boards.add(board);
                }

                if (++count == CRAWL_COUNT) break; // 상위 3개 항목만 추출
            }
        }

        boards.forEach(b-> LOGGER.info(b.toString()));

    }

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        baseUrl = baseUrl.replace(https, "");

        List<Board> boards = new ArrayList<>();

        Document doc = Jsoup.connect(http + baseUrl)
                .get();

        // 'line_bottom' 클래스를 가진 td 태그를 포함하는 tr 태그들만 선택
        Elements rows = doc.select("tr:has(td.line_bottom)");

        int count = 0;
        for (Element row : rows) {
            Elements tds = row.select("td.line_bottom");

            // 행이 비어있지 않고 첫 번째 'td'가 숫자로 시작하는 경우만 처리
            if (!tds.isEmpty() && tds.get(0).text().matches("\\d+")) {
                String numberStr = tds.get(0).text();
                int postNumber = Integer.parseInt(numberStr);
                String title = tds.get(1).text();
                String date = tds.get(2).text();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                LocalDate postAt = LocalDate.parse(date, formatter);

                if(postNumber >= postNum) {
                    Board board = Board.builder()
                            .postNumber(postNumber)
                            .link(https + baseUrl)
                            .writer(null)
                            .title(title)
                            .department(department)
                            .postAt(postAt)
                            .build();

                    boards.add(board);
                }

                if (++count == CRAWL_COUNT) break; // 상위 3개 항목만 추출
            }
        }

        return boards;
    }
}
