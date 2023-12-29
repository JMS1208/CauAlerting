package com.jms.alertmessaging.service.mail;

public interface EmailSender {
    public void sendEmail(String to, String subject, String content);
}
