package com.jms.alertmessaging.controller.api.auth;

import com.jms.alertmessaging.config.security.SecurityConfig;
import com.jms.alertmessaging.dto.auth.sign.code.send.SendCodeResponseDto;
import com.jms.alertmessaging.dto.auth.sign.code.verify.VerifyCodeRequestDto;
import com.jms.alertmessaging.dto.auth.sign.code.verify.VerifyCodeResponseDto;
import com.jms.alertmessaging.dto.student.StudentInfoBundleDto;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.jms.alertmessaging.dto.auth.sign.code.send.SendCodeRequestDto;
import com.jms.alertmessaging.dto.auth.sign.in.SignInRequestDto;
import com.jms.alertmessaging.dto.auth.sign.in.SignInResponseDto;
//import com.jms.alertmessaging.service.auth.AuthService;
import com.jms.alertmessaging.dto.auth.sign.up.SignUpRequestDto;
import com.jms.alertmessaging.dto.auth.sign.up.SignUpResponseDto;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailRequestDto;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponseDto;
import com.jms.alertmessaging.dto.auth.token.TokenRefreshRequestDto;
import com.jms.alertmessaging.dto.auth.token.TokenRefreshResponseDto;
import com.jms.alertmessaging.service.auth.AuthService;
import com.jms.alertmessaging.service.student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth-api")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final EmailSender emailSender;

    @Autowired
    public AuthController(AuthService authService, EmailSender emailSender) {
        this.authService = authService;
        this.emailSender = emailSender;
    }

    //로그인
    @PostMapping("/sign-in")
    public SignInResponseDto signIn(@RequestBody SignInRequestDto requestDto) throws RuntimeException {

        logger.info("[signIn] 로그인을 시도하고 있습니다. email: {}, pw: ****", requestDto.getEmail());

        SignInResponseDto responseDto = authService.signIn(requestDto.getEmail(), requestDto.getPassword());

        if(!responseDto.getAccessToken().isEmpty()) {
            logger.info("[signIn] 정상적으로 로그인되었습니다. email: {}, token: {}", requestDto.getEmail(), responseDto.getAccessToken());
        }

        return responseDto;
    }


    //회원가입
    @PostMapping("/sign-up")
    public SignUpResponseDto signUp(@RequestBody SignUpRequestDto requestDto) {

        logger.info("[signUp] 회원가입 시도, email: {}, departments: {}", requestDto.getEmail(), requestDto.getDepartments());

        SignUpResponseDto responseDto = authService.signUp(requestDto.getEmail(), requestDto.getPassword(), requestDto.getDepartments());

        logger.info("[signUp] 회원가입 완료, email: {}", requestDto.getEmail());

        return responseDto;
    }

    //회원가입시 이메일 중복 확인
    @PostMapping("/check-email")
    public ResponseEntity<?> verifyEmail(@RequestBody CheckEmailRequestDto requestDto) {

        CheckEmailResponseDto responseDto = authService.checkEmailExisted(requestDto.getEmail());

        return ResponseEntity.ok(responseDto);
    }

    //토큰 갱신
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDto requestDto) {
        TokenRefreshResponseDto responseDto = authService.refreshToken(requestDto.getRefreshToken(), requestDto.getAccessToken());

        return ResponseEntity.ok(responseDto);
    }

    //이메일 인증 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequestDto requestDto) {
        logger.info("[sendCode] 이메일 인증 코드 전송: {}", requestDto.getEmail());
        Date expiredAt = authService.sendEmailCode(requestDto.getEmail());

        SendCodeResponseDto responseDto = new SendCodeResponseDto(expiredAt);

        logger.info("[sendCode] 이메일 인증 코드 만료 기간: {}", responseDto.getExpiredAt());
        return ResponseEntity.ok(responseDto);
    }

    //이메일 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequestDto requestDto) {

        logger.info("[verifyCode] 이메일 인증 코드 확인: email {}, code {}", requestDto.getEmail(), requestDto.getCode());
        boolean isValid = authService.verifyEmailCode(requestDto.getEmail(), requestDto.getCode());

        VerifyCodeResponseDto responseDto = new VerifyCodeResponseDto(isValid);

        logger.info("[verifyCode] 이메일 인증 코드 확인 결과: {}", responseDto.isResult());

        return ResponseEntity.ok(responseDto);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
        HttpHeaders responseHeaders = new HttpHeaders();

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        logger.error("ExceptionHandler 호출, {}. {}", e.getCause(), e.getMessage());

        Map<String, String> map = new HashMap<>();

        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");

        return new ResponseEntity<>(map, responseHeaders, httpStatus);

    }
}
