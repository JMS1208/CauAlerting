package com.jms.alertmessaging.repository.post;

import com.jms.alertmessaging.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Board, Long> { }
