package org.registrationservice.util;

import avro.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationCreator {

    public Notification createOAuth2SucessNotification(String familyName, String givenName, String email) {
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
