package com.jms.alertmessaging.component.crawl;

import com.jms.alertmessaging.entity.Post;
import java.io.IOException;
import java.util.List;

public interface WebCrawler {

    public Post crawlPost(int postNumber) throws IOException;

    public List<Post> crawlRecentPostList() throws IOException;
}
