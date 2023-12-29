package com.jms.alertmessaging.security.service;

import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final StudentJpaRepository studentJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info("[loadUserByEmail] 수행: email: {}", email);
        return studentJpaRepository.findByEmail(email);
    }

}
