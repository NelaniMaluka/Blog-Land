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
                "<div style='font-family:Arial,sans-serif;color:#333;background:#f9f9f9;max-width:500px;width:100%;margin:auto;'>"
                        + "<div style='padding:1px 20px 0 20px'><h2 style='margin:20px 0'>Blog-Land</h2></div>"
                        + "<div style='text-align:center;'>"
                        + "<img src='https://blogland.com/assets/logo-heade-2.png' alt='Blog Land Logo' style='width:100%;height:auto;display:block;margin-bottom:20px;'/>"
                        + "</div>"
                        + "<div style='padding:0 20px 40px'>"
                        + "<h2 style='color:#2e86c1;margin:40px 0 20px 0'>Hi there,</h2>"
                        + "<p style='line-height:1.6'>"
                        + "You requested a password reset for your <a href='#' style='color:#2e86c1;text-decoration:none;'>Blog-Land</a> account. "
                        + "Click the button below to reset it:"
                        + "</p>"
                        + "<div style='margin:40px 0;text-align:center;'>"
                        + "<a href='" + resetUrl + "' "
                        + "style='padding:12px 25px;background:#2e86c1;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold;display:inline-block;'>"
                        + "Reset Password</a>"
                        + "</div>"
                        + "<p style='line-height:1.6'>This link will expire in <strong>15 minutes</strong> for your security.</p>"
                        + "<p style='line-height:1.6'>If you didn’t make this request, you can safely ignore this email.</p>"
                        + "<hr style='margin:30px 0;border:none;border-top:1px solid #ccc;'/>"
                        + "<p style='font-size:13px;color:#666;text-align:center;'>"
                        + "Need help? Visit our <a href='https://yourwebsite.com/support' style='color:#2e86c1;text-decoration:none;'>Help Center</a> "
                        + "or reply to this email."
                        + "</p>"
                        + "<div style='text-align:center;margin-top:20px;'>"
                        + "<a href='https://www.facebook.com' style='margin:0 10px;'>"
                        + "<img src='https://cdn-icons-png.flaticon.com/512/733/733547.png' alt='Facebook' width='24' height='24' "
                        + "style='vertical-align:middle;filter:grayscale(100%) brightness(65%);'/></a>"
                        + "<a href='https://www.twitter.com' style='margin:0 10px;'>"
                        + "<img src='https://cdn-icons-png.flaticon.com/512/733/733579.png' alt='Twitter' width='24' height='24' "
                        + "style='vertical-align:middle;filter:grayscale(100%) brightness(65%);'/></a>"
                        + "<a href='https://www.instagram.com' style='margin:0 10px;'>"
                        + "<img src='https://cdn-icons-png.flaticon.com/512/733/733558.png' alt='Instagram' width='24' height='24' "
                        + "style='vertical-align:middle;filter:grayscale(100%) brightness(65%);'/></a>"
                        + "<a href='https://www.linkedin.com' style='margin:0 10px;'>"
                        + "<img src='https://cdn-icons-png.flaticon.com/512/733/733561.png' alt='LinkedIn' width='24' height='24' "
                        + "style='vertical-align:middle;filter:grayscale(100%) brightness(65%);'/></a>"
                        + "</div>"
                        + "<p style='font-size:12px;color:#999;text-align:center;margin-top:15px;'>"
                        + "Blog Land – Where stories find a home."
                        + "</p>"
                        + "</div>"
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
