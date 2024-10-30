package com.example.bibliotech.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;

public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    // Replace these with your actual Gmail app-specific password
    private static final String SENDER_EMAIL = "nguyenhuutruong.96pch@gmail.com"; // Update this
    private static final String APP_PASSWORD = "ctzshuxnmbxvhlne"; // Update this

    public static void sendOTP(String recipientEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset OTP");

            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                            "<h2 style='color: #2C3E50;'>Password Reset Request</h2>" +
                            "<p>Your OTP for password reset is:</p>" +
                            "<h1 style='color: #E74C3C; font-size: 32px; letter-spacing: 5px;'>%s</h1>" +
                            "<p>This OTP will expire in 5 minutes.</p>" +
                            "<p>If you didn't request this password reset, please ignore this email.</p>" +
                            "<p style='color: #7F8C8D; font-size: 12px; margin-top: 20px;'>This is an automated message, please do not reply.</p>" +
                            "</div>",
                    otp
            );

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("OTP email sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            LOGGER.severe("Failed to send email: " + e.getMessage());
            throw e;
        }
    }
}