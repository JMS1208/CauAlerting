package com.jms.alertmessaging.exception.handler;

import com.jms.alertmessaging.dto.base.ResponseDto;
import com.jms.alertmessaging.exception.auth.*;
import com.jms.alertmessaging.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //이메일이 이미 사용 중일 때 - 회원가입 시
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<?> handleEmailAlreadyUsed(EmailAlreadyUsedException e) {

        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("이미 사용 중인 이메일 입니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }

    //이메일 형식이 맞지 않은 경우
    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<?> handleInvalidEmailFormat(InvalidEmailFormatException e) {

        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("이메일 형식을 확인해주세요.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }

    //TODO - 리캡챠 안 쓰면 삭제
    @ExceptionHandler(InvalidReCaptchaTokenException.class)
    public ResponseEntity<String> handleInvalidReCaptchaToken(InvalidReCaptchaTokenException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    //인증 코드와 이메일이 유효하지 않은 경우
    @ExceptionHandler(InvalidCodeWithEmailException.class)
    public ResponseEntity<?> handleInvalidCodeWithEmail(InvalidCodeWithEmailException e) {

        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("인증코드 또는 이메일이 유효하지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }

    //인증되지 않은 사용자
    @ExceptionHandler(InvalidAuthenticationException.class)
    public ResponseEntity<?> handleCustomAuthenticationException(InvalidAuthenticationException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("인증되지 않은 사용자입니다.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseDto);
    }

    //쿠키를 못찾은 경우 - 401 반환
    @ExceptionHandler(CookieNotFoundException.class)
    public ResponseEntity<?> handleCookieNotFoundException(CookieNotFoundException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("쿠키가 유효하지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseDto);
    }

    //토큰이 유효하지 않은 경우 - 401 반환
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("토큰이 유효하지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseDto);
    }

    //로그인 실패 - 비밀번호 또는 이메일이 맞지 않는 경우
    @ExceptionHandler(SignInFailedException.class)
    public ResponseEntity<?> handleSignInFailedException(SignInFailedException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("로그인에 실패하였습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }

    //회원가입 실패 - 학부가 없는 경우,
    @ExceptionHandler(SignUpFailedException.class)
    public ResponseEntity<?> handleSignUpFailedException(SignUpFailedException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("회원가입에 실패하였습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }

    //유저를 찾을 수 없는 경우
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        ResponseDto<String> responseDto = new ResponseDto<>();
        responseDto.setResult(e.getMessage());
        responseDto.setMessage("사용자를 찾을 수 없습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseDto);
    }
}
