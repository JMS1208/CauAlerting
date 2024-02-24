package com.jms.alertmessaging.service.crawl.v1;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException;

}
