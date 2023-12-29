package com.jms.alertmessaging.repository.post;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.board.QBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl extends QuerydslRepositorySupport implements PostRepository {

    public PostRepositoryImpl() {
        super(Board.class);
    }

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Override
    public Optional<Board> findRecentPost() {
        QBoard qBoard = QBoard.board;

        Board recentBoard = from(qBoard)
                .orderBy(qBoard.postNumber.desc())
                .fetchFirst();

        return Optional.ofNullable(recentBoard);
    }

    @Override
    public void savePost(Board board) {
        postJpaRepository.save(board);
    }

    @Override
    public void savePostList(List<Board> boardList) {
        postJpaRepository.saveAll(boardList);
    }


}
