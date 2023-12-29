package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.board.Board;

import java.io.IOException;
import java.util.List;

public interface WebCrawler {

    public Board crawlPost(int postNumber) throws IOException;

    public List<Board> crawlRecentPostList() throws IOException;
}
