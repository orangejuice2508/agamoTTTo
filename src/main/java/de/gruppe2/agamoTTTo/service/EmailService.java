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

/**
 * This service is used for sending emails.
 */
@Slf4j
@Service
public class EmailService {

    private JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.emailSender = javaMailSender;
    }

    /**
     * This method sends an email in HTML format to the specified email address.
     * Without @Async the user which initiated the sending of an email would have to wait until the
     * email was sent.
     *
     * @param to recipient's email address
     * @param subject the subject of the email
     * @param text the text of the email
     */
    @Async
    public void sendHTMLEmail(String to, String subject, String text){
        MimeMessage email = emailSender.createMimeMessage();

        // The creation of the email could throws a MessagingException which has to be caught.
        try{
            // Setting up the email
            email.setSubject(subject);
            email.setRecipient(Message.RecipientType.TO, new InternetAddress(to, false));
            email.setContent(text, "text/html; charset=utf-8");

            // Send the email
            emailSender.send(email);

            // Log that the mail was sent successfully
            log.info("Mail was successfully created and sent!");
        } catch (MessagingException e) {
            // Log that the mail was not sent successfully
            log.error("Mail could not be created or sent!");
        }
    }
}
