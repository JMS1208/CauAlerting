package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    List<Board> crawlFrom(Department department, Integer postNum) throws IOException;

}
