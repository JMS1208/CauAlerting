package com.jms.alertmessaging.component.channel;

import com.jms.alertmessaging.entity.Post;

import java.util.List;

public interface KakaoChannelMessenger {

    public void sendPost(Post post);

    public void sendRecentPostList(List<Post> postList);
}
