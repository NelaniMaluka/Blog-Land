package com.nelani.blog_land_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail( String toEmail, String token){
        String subject = "Password Reset Request";
        String resetUrl = "https://yourfrontend.com/reset-password?token=" + token;
        String body = "Hi there,\n\nClick the link below to reset your password:\n" + resetUrl +
                "\n\nThis link will expire in 15 minutes.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
