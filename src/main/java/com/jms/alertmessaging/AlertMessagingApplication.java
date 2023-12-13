package com.jms.alertmessaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertMessagingApplication.class, args);
    }

}
