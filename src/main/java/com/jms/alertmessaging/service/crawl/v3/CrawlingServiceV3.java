package com.jms.alertmessaging.service.crawl.v3;

public interface CrawlingServiceV3 {

    int START_HOUR = 8;
    int END_HOUR = 21;

    void crawl();

    void addCrawlingListener(CrawlingListener listener);

    void removeCrawlingListener(CrawlingListener listener);
}
