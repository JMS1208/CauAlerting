package com.jms.alertmessaging.service.student;


import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.service.auth.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @BeforeEach
    void setUp() {
        //유저 생성하고
        Student student = new Student();
        student.setId(1L);
        student.setEmail("student@test.com");
        student.setPassword("123123");
        student.setRoles(Collections.singletonList("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(student, null, student.getAuthorities());

        //시큐리티 컨텍스트에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void tearDown() {
        //테스트 끝난 후, SecurityContext를 클리어
        SecurityContextHolder.clearContext();
    }

    @Test
    public void 현재_유저_확인() throws Exception {
        //given
        Student currentStudent = authService.getCurrentUser();

        //when

        //then
        Assertions.assertEquals("student@test.com", currentStudent.getEmail());

    }
}
