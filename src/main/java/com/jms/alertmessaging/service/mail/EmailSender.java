package com.jms.alertmessaging.service.mail;

import java.util.List;

public interface EmailSender {
    void sendEmailToPerson(String address, String title, String content);

    void sendEmailToPeople(List<String> addresses, String title, String content);
}
