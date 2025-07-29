package com.nelani.blog_land_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String subject = "🔐 Blog Land – Reset Your Password";
        String resetUrl = "https://blogland.com/reset-password?token=" + token;

        String htmlContent =
                "<div style='font-family:Arial,sans-serif;color:#333;padding:20px;background:#f9f9f9;'>"
                        + "<div style='text-align:center;'>"
                        + "<img src='https://blogland.com/assets/logo-header.png' alt='Blog Land Logo' style='max-width:200px;margin-bottom:20px;'/>"
                        + "</div>"
                        + "<h2 style='color:#2E86C1;'>Hi there,</h2>"
                        + "<p>You requested a password reset for your Blog Land account. Click the button below to reset it:</p>"
                        + "<div style='margin:30px 0;text-align:center;'>"
                        + "<a href='" + resetUrl + "' style='padding:12px 25px;background:#2E86C1;color:#fff;text-decoration:none;border-radius:5px;'>Reset Password</a>"
                        + "</div>"
                        + "<p>This link will expire in <strong>15 minutes</strong> for your security.</p>"
                        + "<p>If you didn’t make this request, you can safely ignore this email.</p>"
                        + "<hr style='margin:30px 0;border:none;border-top:1px solid #ccc;'/>"
                        + "<p style='font-size:12px;color:#999;'>Blog Land – Where stories find a home.</p>"
                        + "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Enable HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            // Consider logging this for debugging
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
