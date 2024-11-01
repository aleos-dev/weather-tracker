package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.service.EmailServiceException;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;


public class EmailService {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(EmailService.class);

    private static final String SENDER_EMAIL = "WEATHER_TRACKER_MAIL_SERVICE_SENDER";
    private static final String EMAIL_SERVICE_CODE = "WEATHER_TRACKER_MAIL_SERVICE_CODE";

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        var properties = initializeEmailProperties();

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
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

            Transport.send(message);
            logger.info("Verification email sent to {}", toEmail);
        } catch (MessagingException ex) {
            throw new EmailServiceException("Failed to send email", ex);
        }
    }

    private static java.util.Properties initializeEmailProperties() {
        java.util.Properties properties = new java.util.Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host for Gmail
        properties.put("mail.smtp.port", "587"); // TLS port
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS for secure connection
        return properties;
    }
}
