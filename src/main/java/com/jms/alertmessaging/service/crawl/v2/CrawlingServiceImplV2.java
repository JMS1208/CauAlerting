package com.jms.alertmessaging.service.crawl.v2;

import com.jms.alertmessaging.component.crawl.*;
import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.department.Department;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlingServiceImplV2 implements CrawlServiceV2 {

    private final Map<Long, Object> webCrawlers = new HashMap<>();

    private final List<WebCrawlAdapter> webCrawlAdapters = new ArrayList<>();

    @Override
    public List<Board> crawlFrom(Department department, String baseUrl, Integer postNumber, Object handler) throws IOException {

        SoftwareDepWebCrawler webCrawler = (SoftwareDepWebCrawler) handler;

        return webCrawler.crawlFrom(department, baseUrl, postNumber);
    }

    public CrawlingServiceImplV2() {
        initWebCrawlers();
        initWebCrawlAdapters();
    }

    private void initWebCrawlers() {
        webCrawlers.put(1L, new SoftwareDepWebCrawler());
        webCrawlers.put(2L, new BizAdminDepCrawler());
        webCrawlers.put(3L, new NurseDepWebCrawler());
        webCrawlers.put(4L, new MachineEngDepCrawler());
        webCrawlers.put(5L, new ArchiDepCrawler());
        webCrawlers.put(6L, new IntegrativeEngDepCrawler());
        webCrawlers.put(7L, new AdvancedMaterialDepCrawler());
        webCrawlers.put(8L, new ChemicalEngDepCrawler());
        webCrawlers.put(9L, new PublicServiceDepCrawler());
    }

    private void initWebCrawlAdapters() {

    }
}
