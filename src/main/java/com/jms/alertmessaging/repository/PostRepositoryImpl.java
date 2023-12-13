package com.jms.alertmessaging.repository;

import com.jms.alertmessaging.entity.Post;
import com.jms.alertmessaging.entity.QPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl extends QuerydslRepositorySupport implements PostRepository {

    public PostRepositoryImpl() {
        super(Post.class);
    }

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findRecentPost() {
        QPost post = QPost.post;

        Post recentPost = from(post)
                .orderBy(post.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(recentPost);
    }

    @Override
    public void savePost(Post post) {
        postJpaRepository.save(post);
    }

    @Override
    public void savePostList(List<Post> postList) {
        postJpaRepository.saveAll(postList);
    }


}
