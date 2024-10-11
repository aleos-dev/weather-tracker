package com.aleos.service;

import com.aleos.exception.service.EmailServiceException;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;

import java.util.Properties;

public class EmailService {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(EmailService.class);

    private static final String SENDER_EMAIL = "oal.aleos@gmail.com";
    private static final String EMAIL_SERVICE_CODE = System.getenv("weather_tracker_mail_service_code");

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host for Gmail
        properties.put("mail.smtp.port", "587"); // TLS port
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for secure connection

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, EMAIL_SERVICE_CODE);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Email Verification");
            message.setText("Click the link below to verify your account:\n" + verificationUrl);

            Transport.send(message);
        } catch (MessagingException ex) {
            throw new EmailServiceException("Failed to send email", ex);
        }
    }
}
