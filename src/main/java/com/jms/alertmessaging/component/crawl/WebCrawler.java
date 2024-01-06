package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public sealed interface WebCrawler permits NurseDepWebCrawler, SoftwareDepWebCrawler, BizAdminDepCrawler {

    int softInitPostNumber = 2812;

    int bizAdminInitPostNumber = 6811;

    int nurseInitPostNumber = 2552;

    List<Board> crawlFrom(Department department, Integer postNum) throws IOException;

}
