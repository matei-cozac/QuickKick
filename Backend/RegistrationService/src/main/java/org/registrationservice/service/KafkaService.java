package org.registrationservice.service;

import avro.Notification;
import org.registrationservice.kafka.producer.KafkaProducer;
import org.registrationservice.model.Administrator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaService {

    private final KafkaProducer kafkaProducer;

    public KafkaService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendRegisterUserNotification(String lastName, String firstName, String username, UUID token) {
        Notification notification = createSuccessRegisterNotification(lastName, firstName, username, token);
        kafkaProducer.sendMessage("notifications_register", notification);
    }

    public void sendOAuth2Notification(String familyName, String givenName, String email) {
        Notification notification = createOAuth2SucessNotification(givenName, familyName, email);
        kafkaProducer.sendMessage("notifications_register", notification);
    }

    public void sendAdministratorRegisterRequest(Administrator administrator) {
    }

    private Notification createSuccessRegisterNotification(String lastName, String firstName, String email, UUID token) {
        String notificationMessage = this.createRegisterMessage(lastName, firstName, token);
        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setMessage(notificationMessage);
        notification.setError(false);

        return notification;

    }

    private String createRegisterMessage(String lastName, String firstName, UUID token) {
        String fullName = firstName + " " + lastName;
        String message = "Hello " + fullName + "!\n";
        message += "You have successfully registered on QuickKick platform!\n";
        message += "In order to finish your registration, please click on the following link below:\n";
        message += "http://localhost:8080/api/v1.0/auth/confirm-account/" + token + "\n";
        message += "Thank you for using QuickKick and LET'S MOVE!";
        return message;
    }

    private Notification createOAuth2SucessNotification(String familyName, String givenName, String email) {
        String notificationMessage = this.createOAuth2Message(familyName, givenName);
        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setMessage(notificationMessage);
        notification.setError(false);

        return notification;
    }

    private String createOAuth2Message(String familyName, String givenName){
        String fullName = familyName + " " + givenName;
        String message = "Hello " + fullName + "!\n";
        message += "By using your account for logging into QuickKick, automatically an account was created.";
        message += " In order to finish your registration, an phone number needs to be added.\n";
        message += "Thank you for using QuickKick and LET'S MOVE!";
        return message;
    }
}
