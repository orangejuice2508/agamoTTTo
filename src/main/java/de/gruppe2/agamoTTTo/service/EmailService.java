package de.gruppe2.agamoTTTo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Async
    public void sendHTMLEmail(String to, String subject, String text){
        MimeMessage email = mailSender.createMimeMessage();

        try{
            email.setSubject(subject);
            email.setRecipient(Message.RecipientType.TO, new InternetAddress(to, false));
            email.setContent(text, "text/html; charset=utf-8");
        }
        catch(MessagingException e){
            log.info("Mail could not be created!");
        }

        mailSender.send(email);
    }
}
