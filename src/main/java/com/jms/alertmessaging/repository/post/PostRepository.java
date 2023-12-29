package com.jms.alertmessaging.repository.post;

import com.jms.alertmessaging.entity.board.Board;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    //가장 최근에 알림을 보낸 Post
    public Optional<Board> findRecentPost();

    //알림 보낸거 Post 저장
    public void savePost(Board board);

    //알림 보낸거 여러 Post 저장
    public void savePostList(List<Board> boardList);
}