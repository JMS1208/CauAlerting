package com.jms.alertmessaging.controller.api.auth;

import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailRequest;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponse;
import com.jms.alertmessaging.dto.auth.sign.code.send.SendCodeRequest;
import com.jms.alertmessaging.dto.auth.sign.code.send.SendCodeResponse;
import com.jms.alertmessaging.dto.auth.sign.code.verify.VerifyCodeRequest;
import com.jms.alertmessaging.dto.auth.sign.code.verify.VerifyCodeResponse;
import com.jms.alertmessaging.dto.auth.sign.in.SignInRequest;
import com.jms.alertmessaging.dto.auth.sign.up.SignUpRequest;
import com.jms.alertmessaging.dto.base.ResponseDto;
import com.jms.alertmessaging.exception.auth.SignInFailedException;
import com.jms.alertmessaging.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth-api")
@RequiredArgsConstructor
public class AuthController {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    //로그인
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest requestDto, HttpServletResponse response) throws SignInFailedException {

        LOGGER.info("[signIn] 로그인을 시도하고 있습니다. email: {}, pw: ****", requestDto.getEmail());

        authService.signIn(requestDto.getEmail(), requestDto.getPassword(), response);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest requestDto) {

        LOGGER.info("[signUp] 회원가입 시도, email: {}, departments: {}", requestDto.getEmail(), requestDto.getDepartments());

        authService.signUp(requestDto.getEmail(), requestDto.getPassword(), requestDto.getDepartments());

        LOGGER.info("[signUp] 회원가입 완료, email: {}", requestDto.getEmail());

        ResponseDto<Boolean> responseDto = new ResponseDto<>();

        responseDto.setResult(true);
        responseDto.setMessage("회원가입이 완료 되었습니다.");

        return ResponseEntity.ok(responseDto);
    }

    //회원가입시 이메일 중복 확인
    @PostMapping("/check-email")
    public ResponseEntity<?> verifyEmail(@RequestBody CheckEmailRequest requestDto) {

        CheckEmailResponse responseDto = authService.checkEmailExisted(requestDto.getEmail());

        return ResponseEntity.ok(responseDto);
    }

    //이메일 인증 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequest requestDto) {
        LOGGER.info("[sendCode] 이메일 인증 코드 전송: {}", requestDto.getEmail());
        Date expiredAt = authService.sendEmailCode(requestDto.getEmail());

        SendCodeResponse responseDto = new SendCodeResponse(expiredAt);

        LOGGER.info("[sendCode] 이메일 인증 코드 만료 기간: {}", responseDto.getExpiredAt());
        return ResponseEntity.ok(responseDto);
    }

    //이메일 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest requestDto) {

        LOGGER.info("[verifyCode] 이메일 인증 코드 확인: email {}, code {}", requestDto.getEmail(), requestDto.getCode());
        boolean isValid = authService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());

        VerifyCodeResponse responseDto = new VerifyCodeResponse(isValid);

        LOGGER.info("[verifyCode] 이메일 인증 코드 확인 결과: {}", responseDto.isResult());

        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/csrf-token")
    public CsrfToken csrf(CsrfToken token) {
        LOGGER.info("[CSRF 토큰 발급] : {}", token);

        return token; // 현재 세션의 CSRF 토큰 반환
    }

}
