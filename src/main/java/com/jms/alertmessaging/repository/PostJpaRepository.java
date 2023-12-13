package com.jms.alertmessaging.repository;

import com.jms.alertmessaging.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> { }
