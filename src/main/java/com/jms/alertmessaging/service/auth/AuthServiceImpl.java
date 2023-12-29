package com.jms.alertmessaging.service.auth;

import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.service.enrollment.EnrollmentService;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.jms.alertmessaging.dto.auth.sign.in.SignInResponseDto;
import com.jms.alertmessaging.dto.auth.sign.up.SignUpResponseDto;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponseDto;
import com.jms.alertmessaging.dto.auth.token.Token;
import com.jms.alertmessaging.dto.auth.token.TokenRefreshResponseDto;
import com.jms.alertmessaging.dto.auth.token.Tokens;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.security.jwt.JwtTokenProvider;
import com.jms.alertmessaging.service.redis.RedisService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final StudentJpaRepository studentJpaRepository;

    private final DepartmentJpaRepository departmentJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final RedisService redisService;

    private final EnrollmentService enrollmentService;

    @Autowired
    public AuthServiceImpl(StudentJpaRepository studentJpaRepository, DepartmentJpaRepository departmentJpaRepository,
                           JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
                           EmailSender emailSender, RedisService redisService, EnrollmentService enrollmentService) {
        this.studentJpaRepository = studentJpaRepository;
        this.departmentJpaRepository = departmentJpaRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.redisService = redisService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public SignInResponseDto signIn(String email, String password) throws RuntimeException {
        logger.info("[getSignInResult] signDataHandler 로 회원 정보 요청");

        Student student = studentJpaRepository.findByEmail(email);

        logger.info("[getSignInResult] email: {}", email);

        logger.info("[getSignInResult] 패스워드 비교 수행");

        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new RuntimeException();
        }

        logger.info("[getSignInResult] 패스워드 일치");

        logger.info("[getSignInResult] SignInResultDto 객체 생성");

        Token accessToken = jwtTokenProvider.createAccessToken(student.getEmail(), student.getRoles());
        Token refreshToken = jwtTokenProvider.createRefreshToken(student.getEmail(), student.getRoles());

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .accessToken(accessToken.token)
                .accessTokenExpiredAt(accessToken.expiredAt)
                .refreshToken(refreshToken.token)
                .refreshTokenExpiredAt(refreshToken.expiredAt)
                .build();

        logger.info("[getSignInResult] SignInResultDto 객체 값 주입");
        return signInResponseDto;
    }

    @Transactional
    @Override
    public SignUpResponseDto signUp(String email, String password, Set<Long> departments) {
        logger.info("[getSignUpResult] 회원가입 정보 전달");
        //TODO - 나중에 오류 코드, 메시지 정리해서 아래 Dto 수정 필요
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();

        //이미 가입된 이메일인지 확인
        if(studentJpaRepository.existsByEmail(email)) {
            logger.info("[getSignUpResult] 실패 처리 완료 - 이미 존재하는 이메일");
            signUpResponseDto.setResult(false);
            return signUpResponseDto;
        }

        Set<Department> departmentSet = departmentJpaRepository.findByIdIn(departments);

        logger.info("[signUp] 찾은 학부 수: {}", departmentSet.size());

        if (departmentSet.isEmpty()) {
            logger.info("[getSignUpResult] 실패 처리 완료 - 학부 없음");
            signUpResponseDto.setResult(false);
            return signUpResponseDto;
        }

        Student student = Student.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        studentJpaRepository.save(student);
        enrollmentService.saveEnrollments(student, departmentSet);

        signUpResponseDto.setResult(true);
        return signUpResponseDto;
    }

    @Override
    public CheckEmailResponseDto checkEmailExisted(String email) {

        boolean isExisted = studentJpaRepository.existsByEmail(email);

        return new CheckEmailResponseDto(isExisted);
    }

    @Override
    public TokenRefreshResponseDto refreshToken(String accessToken, String refreshToken) {
        Tokens newTokens = jwtTokenProvider.refreshTokens(accessToken, refreshToken);
        return TokenRefreshResponseDto.fromTokens(newTokens);
    }

    @Override
    public Date sendEmailCode(String email) {
        String code = createVerificationCode();
        long fiveMinutesLater = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        Date expiredAt = new Date(fiveMinutesLater);
        emailSender.sendEmail(email, "중앙대 학부 공지 알리미 회원가입을 위한 인증코드입니다.", "인증코드: " + code);
        redisService.putStringKeyStringValue(email, code, expiredAt);
        logger.info("이메일 인증코드 전송: email {}, code {}, expiredAt {}", email, code, expiredAt);
        return expiredAt;
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {

        String savedCode = redisService.getStringValue(email);

        if (savedCode == null) {
            return false;
        }

        return code.equals(savedCode);
    }


    //인증코드 6자리 숫자 만들기
    private String createVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(1000000);
        return String.format("%06d", code);
    }
}
