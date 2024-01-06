package com.jms.alertmessaging.repository.keyword;

import com.jms.alertmessaging.entity.keyword.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordJpaRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByEnrollment_Id(long enrollmentId);
    void deleteAllByEnrollment_Id(long enrollmentId);
}
