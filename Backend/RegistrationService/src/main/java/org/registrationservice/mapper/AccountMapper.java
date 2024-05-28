package org.registrationservice.mapper;

import org.registrationservice.dto.RegisterAdministratorDTO;
import org.registrationservice.dto.RegisterUserDTO;
import org.registrationservice.model.Account;
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
    public Account fromRegisterUserDTO(RegisterUserDTO registerUserDTO){
        return Account
                .builder()
                .email(registerUserDTO.getEmail())
                .password(registerUserDTO.getPassword())
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .build();
    }

    /**
     * Builds a entity of Account from an DTO of type RegisterAdministratorDTO.
     * @param registerAdministratorDTO
     * The DTO received from the HTTP requests.
     * @return
     * An entity object of type Account, which contains the information from the DTO.
     */
    public Account fromRegisterAdministratorDTO(RegisterAdministratorDTO registerAdministratorDTO){
        return Account
                .builder()
                .email(registerAdministratorDTO.getEmail())
                .password(registerAdministratorDTO.getPassword())
                .phoneNumber(registerAdministratorDTO.getPhoneNumber())
                .build();
    }
}
