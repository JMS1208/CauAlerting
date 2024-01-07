package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public sealed interface WebCrawler permits NurseDepWebCrawler, SoftwareDepWebCrawler, BizAdminDepCrawler {

    int SOFT_INIT_POST_NUM = 2812;

    int BIZ_ADMIN_INIT_POST_NUM = 6811;

    int NURSE_ADMIN_INIT_POST_NUM = 2552;

    List<Board> crawlFrom(Department department, Integer postNum) throws IOException;

}
