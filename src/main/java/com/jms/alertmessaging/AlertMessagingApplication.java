package com.jms.alertmessaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class AlertMessagingApplication {


    private static final Logger logger = LoggerFactory.getLogger(AlertMessagingApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(AlertMessagingApplication.class, args);



    }



}
