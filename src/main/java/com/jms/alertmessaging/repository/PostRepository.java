package com.jms.alertmessaging.repository;

import com.jms.alertmessaging.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    //가장 최근에 알림을 보낸 Post
    public Optional<Post> findRecentPost();

    //알림 보낸거 Post 저장
    public void savePost(Post post);

    //알림 보낸거 여러 Post 저장
    public void savePostList(List<Post> postList);
}