package com.jms.alertmessaging.config.security;

import com.jms.alertmessaging.component.jwt.JwtProvider;
import com.jms.alertmessaging.exception.handler.CustomAccessDeniedHandler;
import com.jms.alertmessaging.exception.handler.CustomAuthenticationEntryPoint;
import com.jms.alertmessaging.filterchain.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtProvider jwtProvider;

    public static final String[] PUBLIC_URLS = {"/auth-api/**", "/exception", "/error"};

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

        //쿠키를 사용하는 경우 CSRF 공격에 취약해질 수 있기 떄문에 CSRF 보호 매커니즘 필요
//        http.csrf( auth -> auth
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .ignoringRequestMatchers(PUBLIC_URLS)
//        );
        http.csrf(AbstractHttpConfigurer::disable);


        //Rest API 기반의 애플리케이션 동작 방식을 설정
        //세션 방식은 쓰지 않기 때문에 STATELESS
        http.sessionManagement(auth -> auth
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        //api 접근 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
        );

        //권한 확인 과정에서 통과하지 못하는 예외가 발생할 때, 예외를 전달
        http.exceptionHandling(auth -> auth
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        //인증 과정에서 예외가 발생할 경우 예외를 전달
        http.exceptionHandling(auth -> auth
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    //https 리디렉션
    @Bean
    public ServletWebServerFactory servletContainer() {
        // Enable SSL Trafic
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        // Http -> Https 리디렉션
        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());

        return tomcat;
    }

    //Connector는 네트워크 연결을 처리하는 엔드포인트(end-point)로서의 역할
    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}


