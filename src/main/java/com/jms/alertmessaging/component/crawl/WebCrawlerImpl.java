package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebCrawlerImpl implements WebCrawler {

    public static final Logger logger = LoggerFactory.getLogger(WebCrawlerImpl.class);

    @Override
    public Post crawlPost(int postNumber) throws IOException {

        String http = "http";
        String https = "https";

        String url = "://cse.cau.ac.kr/sub05/sub0501.php?nmode=view&code=oktomato_bbs05&uid=" + postNumber;
        Document doc = Jsoup.connect(http + url).get();

        Element titleElement = doc.select("div.header > h3").first();
        Element dateElement = doc.select("div.header span:nth-child(1)").first();
        Element authorElement = doc.select("div.header > div > span:nth-child(3)").first();

        String title = titleElement.text();
        String author = authorElement.text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateElement.text(), formatter);

        return Post.builder()
                .postNumber(postNumber)
                .title(title)
                .author(author)
                .date(date)
                .link(https + url)
                .build();
    }

    @Override
    public List<Post> crawlRecentPostList() throws IOException {
        //얘는 디비 거칠 필요 없음 그냥 바로 보내줌
        String http = "http";
        String https = "https";

        String url = "://cse.cau.ac.kr/sub05/sub0501.php";
        Document doc = Jsoup.connect(http + url).get();

        Elements rows = doc.select("table.table-basic tbody tr");

        List<Post> postList = new ArrayList<>();

        for (Element row : rows) {
            // 각 행에서 원하는 정보 추출
            String title = row.select("td.aleft a").text();
            String author = row.select("td.pc-only:nth-child(4)").text();
            String dateStr = row.select("td.pc-only:nth-child(5)").text();
            String link = row.select("td.aleft a").attr("href");

            //postNumber 추출
            Pattern pattern = Pattern.compile("uid=(\\d+)");
            Matcher matcher = pattern.matcher(link);

            if(!matcher.find()) {
                throw new IOException();
            }

            int postNumber = Integer.parseInt(matcher.group(1));

            //날짜 추출
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            Post post = Post.builder()
                    .author(author)
                    .postNumber(postNumber)
                    .link(https + link)
                    .title(title)
                    .date(date)
                    .build();

            postList.add(post);
        }

        postList.sort(Post::compareTo);

        return postList;
    }
}
