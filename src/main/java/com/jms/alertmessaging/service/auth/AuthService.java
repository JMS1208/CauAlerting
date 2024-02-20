package com.jms.alertmessaging.service.auth;

import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponse;
import com.jms.alertmessaging.entity.student.Student;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Set;

public interface AuthService {

    Student getCurrentUser();

    void signIn(String email, String password, HttpServletResponse response) throws RuntimeException;

    void signOut(HttpServletResponse response);

    void signUp(String email, String password, Set<Long> departments);

    void deleteUser(HttpServletResponse response);

    CheckEmailResponse checkEmailExisted(String email);

//    TokenRefreshResponseDto refreshToken(String accessToken, String refreshToken);

    Date sendEmailCode(String email);

    boolean verifyEmailCode(String email, String code);

    Authentication getCurrentUserAuthentication();
}
