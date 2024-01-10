package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;

import java.io.IOException;
import java.util.List;

public sealed interface WebCrawler permits AdvancedMaterialDepCrawler, ArchiDepCrawler, BizAdminDepCrawler, IntegrativeEngDepCrawler, MachineEngDepCrawler, NurseDepWebCrawler, SoftwareDepWebCrawler {

    //소프트웨어학부 첫 포스트 넘버
    int SOFT_INIT_POST_NUM = 2812;

    //경영학과 첫 포스트 넘버
    int BIZ_ADMIN_INIT_POST_NUM = 6811;

    //간호학과 첫 포스트 넘버
    int NURSE_ADMIN_INIT_POST_NUM = 2552;

    //기계공학과 첫 포스트 넘버 - 학부 공지사항
    int MACHINE_ENG_DEP_INIT_POST_NUM = 4686;

    //기계공학과 공학인증 공지사항
    int MACHINE_ENG_CERT_INIT_POST_NUM = 61;

    //기계공학과 취업 공지사항
    int MACHINE_ENG_JOB_INIT_POST_NUM = 4649;


    String http = "http";

    String https = "https";



    int CRAWL_COUNT = 3;

    List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException;

}
