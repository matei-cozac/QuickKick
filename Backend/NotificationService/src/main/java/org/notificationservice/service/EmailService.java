package org.notificationservice.service;

import avro.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    /**
     * Method that will send the registration mail. The mail will contain a link in order to confirm the account.
     * @param notification
     * The notification object containing the email address of the user and the message.
     */
    public void sendRegisterMail(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(notification.getEmail());
            message.setSubject("Registration");
            message.setText(notification.getMessage());
            mailSender.send(message);
        } catch (Exception e) {
            // Handle exception, log it, etc.
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
