package com.jms.alertmessaging.service.crawl.v2;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public interface CrawlServiceV2 {

    List<Board> crawlFrom(Department department, String baseUrl, Integer postNumber, Object webCrawler) throws IOException;

}
