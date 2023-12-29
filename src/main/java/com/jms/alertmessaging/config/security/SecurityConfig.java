package com.jms.alertmessaging.config.security;

import com.jms.alertmessaging.security.handler.exception.CustomAccessDeniedHandler;
import com.jms.alertmessaging.security.handler.exception.CustomAuthenticationEntryPoint;
import com.jms.alertmessaging.security.jwt.JwtAuthenticationFilter;
import com.jms.alertmessaging.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenProvider jwtTokenProvider;

    public static final String[] PUBLIC_URLS = { "/auth-api/**", "/api/**", "/exception" , "/error" };

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //사용자 비밀번호에 쓸 것
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //Rest api 에서는 필요 없어서 CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //Rest API 기반의 애플리케이션 동작 방식을 설정
        //세션 방식은 쓰지 않기 때문에 STATELESS
        http.sessionManagement(auth -> auth
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        //api 접근 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().hasRole("USER")
        );

        //권한 확인 과정에서 통과하지 못하는 예외가 발생할 때, 예외를 전달
        http.exceptionHandling(auth-> auth
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        //인증 과정에서 예외가 발생할 경우 예외를 전달
        http.exceptionHandling(auth-> auth
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
