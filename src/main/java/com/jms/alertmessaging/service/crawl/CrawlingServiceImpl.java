package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.component.crawl.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingServiceImpl.class);

    private final ApplicationContext context;

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNum) throws IOException {

        WebCrawler webCrawler = getWebCrawler(department);

        return webCrawler.crawlFrom(department, baseUrl, postNum);
    }

    //[important] 새 학부 추가할 때마다 이곳 조정해주어야한다
    private WebCrawler getWebCrawler(Department department) {
        return switch ((int) department.getId()) {
            case 1 -> context.getBean(SoftwareDepWebCrawler.class);
            case 2 -> context.getBean(BizAdminDepCrawler.class);
            case 3 -> context.getBean(NurseDepWebCrawler.class);
            case 4 -> context.getBean(MachineEngDepCrawler.class);
            case 5 -> context.getBean(ArchiDepCrawler.class);
            case 6 -> context.getBean(IntegrativeEngDepCrawler.class);
            case 7 -> context.getBean(AdvancedMaterialDepCrawler.class);
            case 8 -> context.getBean(ChemicalEngDepCrawler.class);
            default -> throw new IllegalArgumentException("Invalid department ID");
        };
    }

}
