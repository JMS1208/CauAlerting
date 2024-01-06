package com.jms.alertmessaging.repository.post;

import com.jms.alertmessaging.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepository extends JpaRepository<Board, Long> { }
