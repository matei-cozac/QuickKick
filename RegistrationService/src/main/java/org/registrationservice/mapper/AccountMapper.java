package org.registrationservice.mapper;

import org.registrationservice.dto.RegisterUserDTO;
import org.registrationservice.model.Account;
import org.registrationservice.model.Role;
import org.registrationservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    /**
     * Builds a entity of Account from an DTO of type RegisterUserDTO.
     * @param registerUserDTO
     * The DTO received from the HTTP requests.
     * @return
     * An entity object of type Account, which contains the information from the DTO.
     */
    public Account fromRegisterDTO(RegisterUserDTO registerUserDTO){
        return Account
                .builder()
                .email(registerUserDTO.getEmail())
                .password(registerUserDTO.getPassword())
                .build();
    }
}
