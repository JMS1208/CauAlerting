package com.jms.alertmessaging.service.crawl.v3;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.util.List;

public interface CrawlingListener {

    void beforeCrawl(Department department);

    void afterCrawl(Department department, List<Board> boards);

    void beforeSave(Department department, List<Board> boards);

    void afterSave(Department department, List<Board> boards);
}
