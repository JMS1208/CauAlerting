package com.jms.alertmessaging.service.auth;

import com.jms.alertmessaging.component.jwt.JwtProvider;
import com.jms.alertmessaging.data.token.Token;
import com.jms.alertmessaging.dto.auth.sign.check.CheckEmailResponse;
import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.enrollment.Enrollment;
import com.jms.alertmessaging.entity.student.Frequency;
import com.jms.alertmessaging.entity.student.Student;
import com.jms.alertmessaging.exception.auth.EmailAlreadyUsedException;
import com.jms.alertmessaging.exception.auth.InvalidAuthenticationException;
import com.jms.alertmessaging.exception.auth.SignInFailedException;
import com.jms.alertmessaging.exception.auth.SignUpFailedException;
import com.jms.alertmessaging.repository.department.DepartmentJpaRepository;
import com.jms.alertmessaging.repository.enrollment.EnrollmentJpaRepository;
import com.jms.alertmessaging.repository.keyword.KeywordJpaRepository;
import com.jms.alertmessaging.repository.student.StudentJpaRepository;
import com.jms.alertmessaging.service.enrollment.EnrollmentService;
import com.jms.alertmessaging.service.mail.EmailSender;
import com.jms.alertmessaging.service.redis.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImplV2 implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImplV2.class);

    private final StudentJpaRepository studentJpaRepository;
    private final EnrollmentJpaRepository enrollmentJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final KeywordJpaRepository keywordJpaRepository;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final RedisService redisService;

    private final EnrollmentService enrollmentService;

    @Override
    public Student getCurrentUser() throws AuthenticationException {

        Authentication authentication = getCurrentUserAuthentication();

        Object principal = authentication.getPrincipal();

        if(principal instanceof Student) {
            return (Student) principal;
        } else {
            throw new InvalidAuthenticationException("인증된 유저 정보를 가져오는 데 실패했습니다.");
        }

    }

    @Override
    public void signIn(String email, String password, HttpServletResponse response) throws SignInFailedException {
        logger.info("[getSignInResult] signDataHandler 로 회원 정보 요청");

        Student student = studentJpaRepository.findByEmail(email).orElseThrow(() -> new SignInFailedException("이메일 또는 비밀번호를 확인해주세요."));

        logger.info("[getSignInResult] email: {}", email);

        logger.info("[getSignInResult] 패스워드 비교 수행");

        if (!passwordEncoder.matches(password, student.getPassword()))
            throw new SignInFailedException("이메일 또는 비밀번호를 확인해주세요.");


        logger.info("[getSignInResult] 패스워드 일치");

        logger.info("[getSignInResult] SignInResultDto 객체 생성");

        Token accessToken = jwtProvider.createAccessToken(student.getEmail(), student.getRoles());
        Token refreshToken = jwtProvider.createRefreshToken(student.getEmail(), student.getRoles());

        jwtProvider.setTokenToCookie(accessToken.key, accessToken.value, JwtProvider.ACCESS_TOKEN_COOKIE_DURATION, response);
        jwtProvider.setTokenToCookie(refreshToken.key, refreshToken.value, JwtProvider.REFRESH_TOKEN_COOKIE_DURATION, response);


        Authentication authentication = jwtProvider.getAuthentication(accessToken.value);

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    //쿠키에서 만료된 토큰 입력하고, 시큐리티 컨텍스트에서도 인증 정보 삭제
    @Override
    public void signOut(HttpServletResponse response) {
        jwtProvider.removeTokensFromCookie(response);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    @Override
    public void signUp(String email, String password, Set<Long> departments) throws EmailAlreadyUsedException, SignUpFailedException {
        logger.info("[getSignUpResult] 회원가입 정보 전달");

        //이미 가입된 이메일인지 확인
        if (studentJpaRepository.existsByEmail(email)) throw new EmailAlreadyUsedException("이미 존재하는 이메일입니다.");

        Student student = Student.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .frequency(Frequency.defaultValue())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        studentJpaRepository.save(student);

        Set<Department> departmentSet = departmentJpaRepository.findByIdIn(departments);
        logger.info("[signUp] 찾은 학부 수: {}", departmentSet.size());
        //학부가 있다면
        if (!departmentSet.isEmpty()) {
            enrollmentService.saveEnrollments(student, departmentSet);
        }

    }

    @Override
    public CheckEmailResponse checkEmailExisted(String email) {

        boolean isExisted = studentJpaRepository.existsByEmail(email);

        return new CheckEmailResponse(isExisted);
    }


    @Override
    public Authentication getCurrentUserAuthentication() throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //인증되지 않은 사용자인 경우
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidAuthenticationException("인증되지 않은 유저입니다.");
        }

        return authentication;
    }

    @Override
    public Date sendEmailCode(String email) {
        String code = createVerificationCode();
        long fiveMinutesLater = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        Date expiredAt = new Date(fiveMinutesLater);
        emailSender.sendEmailToPerson(email, "중앙대 학부 공지 알리미 회원가입을 위한 인증코드입니다.", "인증코드: " + code);
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

    //회원탈퇴
    @Override
    public void deleteUser(HttpServletResponse response) {
        Authentication authentication = getCurrentUserAuthentication();
        String email = authentication.getName();

        Student student = studentJpaRepository.findByEmail(email).orElseThrow(() -> new InvalidAuthenticationException("존재하지 않는 유저입니다."));

        Set<Enrollment> enrollments = enrollmentJpaRepository.findByStudentId(student.getId());

        for (Enrollment enrollment : enrollments) {
            keywordJpaRepository.deleteAllByEnrollment_Id(enrollment.getId());
            enrollmentJpaRepository.delete(enrollment);
        }

        studentJpaRepository.delete(student);

        jwtProvider.removeTokensFromCookie(response);
        SecurityContextHolder.clearContext();
    }

    //인증코드 6자리 숫자 만들기
    private String createVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(1000000);
        return String.format("%06d", code);
    }
}
