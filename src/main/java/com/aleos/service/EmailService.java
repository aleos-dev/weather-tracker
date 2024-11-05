package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.service.EmailServiceException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailService {
    private static final String SENDER_EMAIL = "WEATHER_TRACKER_MAIL_SERVICE_SENDER";
    private static final String EMAIL_SERVICE_CODE = "WEATHER_TRACKER_MAIL_SERVICE_CODE";

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        log.info("Preparing to send verification email to {}", toEmail);

        var properties = initializeEmailProperties();
        log.debug("Email properties initialized for SMTP connection: {}", properties);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                log.debug("Authenticating email session with provided credentials");
                return new PasswordAuthentication(
                        Properties.get(SENDER_EMAIL).orElseThrow(),
                        Properties.get(EMAIL_SERVICE_CODE).orElseThrow());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Email Verification");
            message.setText("Click the link below to verify your account:\n" + verificationUrl);

            log.debug("Email message prepared with subject and recipient information for {}", toEmail);

            Transport.send(message);
            log.info("Verification email sent successfully to {}", toEmail);
        } catch (MessagingException ex) {
            throw new EmailServiceException("Failed to send verification email to %s".formatted(toEmail), ex);
        }
    }

    private static java.util.Properties initializeEmailProperties() {
        log.debug("Initializing SMTP properties for email session");

        java.util.Properties properties = new java.util.Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host for Gmail
        properties.put("mail.smtp.port", "587"); // TLS port
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for secure connection

        log.debug("SMTP properties set: host={}, port={}, auth={}, starttls={}",
                properties.get("mail.smtp.host"),
                properties.get("mail.smtp.port"),
                properties.get("mail.smtp.auth"),
                properties.get("mail.smtp.starttls.enable"));
        return properties;
    }
}
