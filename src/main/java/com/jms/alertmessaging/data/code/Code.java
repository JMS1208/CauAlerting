//package com.jms.alertmessaging.data.code;
//
//import com.jms.alertmessaging.exception.base.GeneralException;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//
//import java.util.Optional;
//import java.util.function.Predicate;
//
//@Getter
//@RequiredArgsConstructor
//public enum Code {
//
//    //성공
//    OK(0, HttpStatus.OK, "Ok"),
//
//    //잘못된 요청
//    BAD_REQUEST(100, HttpStatus.BAD_REQUEST, "Bad Request"),
//
//    //유저를 못찾았다거나..
//    NOT_FOUND(101, HttpStatus.NOT_FOUND, "Requested resource is not found"),
//
//    //내부 오류
//    INTERNAL_ERROR(200, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error"),
//
//    //유저 인증 오류
//    UNAUTHORIZED(300, HttpStatus.UNAUTHORIZED, "User unauthorized"),
//
//    //접근 권한 없음
//    FORBIDDEN(301, HttpStatus.FORBIDDEN, "Resource is forbidden");
//
//    private final Integer code;
//    private final HttpStatus httpStatus;
//    private final String message;
//
//    public String getMessage(Throwable e) {
//        return this.getMessage(this.getMessage() + "-" + e.getMessage());
//    }
//
//    public String getMessage(String message) {
//        return Optional.ofNullable(message)
//                .filter(Predicate.not(String::isBlank))
//                .orElse(this.getMessage());
//    }
//
//    public static Code valueOf(HttpStatus httpStatus) {
//        if(httpStatus == null) {
//            throw new GeneralException("Http ")
//        }
//    }
//}
