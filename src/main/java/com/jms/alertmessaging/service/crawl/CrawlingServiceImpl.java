package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.component.crawl.WebCrawler;
import com.jms.alertmessaging.entity.board.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CrawlingServiceImpl implements CrawlingService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingServiceImpl.class);

    @Autowired
    private WebCrawler webCrawler;

    @Override
    public List<Board> crawlPostList() throws IOException {

        //최근꺼 크롤링 - 얘는 제목이 잘려서 못씀
        return webCrawler.crawlRecentPostList();

    }

    @Override
    public Board crawlPost(int postNumber) throws IOException {
        return webCrawler.crawlPost(postNumber);
    }
}
