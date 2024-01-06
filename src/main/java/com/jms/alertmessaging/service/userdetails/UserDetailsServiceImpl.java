package com.jms.alertmessaging.service.userdetails;

import com.jms.alertmessaging.exception.auth.InvalidAuthenticationException;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final StudentJpaRepository studentJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        logger.info("[loadUserByEmail] 수행: email: {}", email);
        return studentJpaRepository.findByEmail(email).orElseThrow(()-> new InvalidAuthenticationException("존재하지 않는 이메일 입니다."));
    }

}
