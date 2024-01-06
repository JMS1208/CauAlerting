package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.component.crawl.BizAdminDepCrawler;
import com.jms.alertmessaging.component.crawl.NurseDepWebCrawler;
import com.jms.alertmessaging.component.crawl.SoftwareDepWebCrawler;
import com.jms.alertmessaging.component.crawl.WebCrawler;
import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingServiceImpl implements CrawlingService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingServiceImpl.class);

    private final ApplicationContext context;

    @Override
    public List<Board> crawlFrom(Department department, Integer postNum) throws IOException {

        WebCrawler webCrawler = switch ((int) department.getId()) {
            case 1 -> context.getBean(SoftwareDepWebCrawler.class);
            case 2 -> context.getBean(BizAdminDepCrawler.class);
            case 3 -> context.getBean(NurseDepWebCrawler.class);
            default -> throw new IllegalArgumentException("Invalid department ID");
        };

        return webCrawler.crawlFrom(department, postNum);
    }

}
