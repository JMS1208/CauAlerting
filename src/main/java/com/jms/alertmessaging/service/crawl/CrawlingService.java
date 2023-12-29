package com.jms.alertmessaging.service.crawl;

import com.jms.alertmessaging.entity.board.Board;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    public List<Board> crawlPostList() throws IOException;

    public Board crawlPost(int postNumber) throws IOException;
}
