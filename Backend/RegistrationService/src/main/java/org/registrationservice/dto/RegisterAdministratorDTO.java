package org.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdministratorDTO {

    private String email;

    private String password;

    private String phoneNumber;

    private String businessName;

    private String cui;

    private String iban;

    private String address;
}
