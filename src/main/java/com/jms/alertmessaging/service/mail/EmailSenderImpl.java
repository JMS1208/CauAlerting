package com.jms.alertmessaging.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {


    private final JavaMailSender mailSender;


    //to: 주소, subject: 제목, content: 내용
    @Async
    @Override
    public void sendEmailToPerson(String address, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(address);
        message.setSubject(title);
        message.setText(content);

        mailSender.send(message);
    }

    @Override
    public void sendEmailToPeople(List<String> addresses, String title, String content) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(addresses.toArray(new String[0]));
        message.setSubject(title);
        message.setText(content);

        mailSender.send(message);
    }
}
