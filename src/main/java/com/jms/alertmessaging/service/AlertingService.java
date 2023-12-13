package com.jms.alertmessaging.service;

import com.jms.alertmessaging.component.channel.KakaoChannelMessenger;
import com.jms.alertmessaging.entity.Post;
import com.jms.alertmessaging.component.crawl.WebCrawler;
import com.jms.alertmessaging.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AlertingService {

    private static final Logger logger = LoggerFactory.getLogger(AlertingService.class);

    @Autowired
    private WebCrawler webCrawler;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KakaoChannelMessenger kakaoChannelMessenger;

    //주기적으로 크롤링해서 메시지 보내고 디비에 저장하기
    @Scheduled(fixedRate = 5000)
    public void repeatCrawlingAndMessaging() {
        try  {
            //디비에서 최근에 전송한 Post 가져오기
            Optional<Post> recentPost = postRepository.findRecentPost();

            //디비에 최근에 전송한 Post 가 없는 경우
            if(recentPost.isEmpty()) {
                //최근꺼 크롤링 해서
                List<Post> recentPostList = webCrawler.crawlRecentPostList();

                //모두 전송
                kakaoChannelMessenger.sendRecentPostList(recentPostList);

                //디비에 저장
                postRepository.savePostList(recentPostList);
                return;
            }

            //다음 포스트 넘버 크롤링 해오기
            int nextPostNumber = recentPost.get().postNumber+1;
            Post post = webCrawler.crawlPost(nextPostNumber);

            //전송
            kakaoChannelMessenger.sendPost(post);

            //디비에 저장
            postRepository.savePost(post);

            logger.info(post.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}