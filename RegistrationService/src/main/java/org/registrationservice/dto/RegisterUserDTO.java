package org.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO {

    /**
     * Email of the user. Will act as the username for the login.
     * Email should be a valid email format.
     * Email should not be null or empty.
     */
    private String email;

    /**
     * Password of the user.
     * Password should not be null or empty.
     * Password should be at least 8 characters.
     */
    private String password;

    /**
     * First name of the user.
     * First name should not be null or empty string.
     */
    private String firstName;

    /**
     * Last name of the user.
     * Last name should not be null or empty string.
     */
    private String lastName;

    /**
     * Phone number of the user.
     * Last name should not be null or empty string.
     */
    private String phoneNumber;
}
