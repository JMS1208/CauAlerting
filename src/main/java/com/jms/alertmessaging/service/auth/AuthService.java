package com.jms.alertmessaging.service.auth;

import com.jms.alertmessaging.dto.auth.sign.in.SignInResponseDto;
import com.jms.alertmessaging.dto.auth.sign.up.SignUpResponseDto;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponseDto;
import com.jms.alertmessaging.dto.auth.token.TokenRefreshResponseDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AuthService {

    public SignInResponseDto signIn(String email, String password) throws RuntimeException;

    public SignUpResponseDto signUp(String email, String password, Set<Long> departments);

//    public DeleteUserResponseDto deleteUser(String email);

    public CheckEmailResponseDto checkEmailExisted(String email);

    public TokenRefreshResponseDto refreshToken(String accessToken, String refreshToken);

    public Date sendEmailCode(String email);

    public boolean verifyEmailCode(String email, String code);
}
