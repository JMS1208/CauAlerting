package com.jms.alertmessaging.component.channel;

import com.jms.alertmessaging.entity.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KakaoChannelMessengerImpl implements KakaoChannelMessenger{
    @Override
    public void sendPost(Post post) {

    }

    @Override
    public void sendRecentPostList(List<Post> postList) {

    }
}
