package org.registrationservice.mapper;

import org.registrationservice.dto.RegisterUserDTO;
import org.registrationservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Builds an entity of User from an DTO of type RegisterUserDTO.
     * @param registerUserDTO
     * The DTO received from the HTTP requests.
     * @return
     * An entity object of type User, which contains the information from the DTO.
     */
    public User fromRegisterDTO(RegisterUserDTO registerUserDTO){
        return User
                .builder()
                .firstName(registerUserDTO.getFirstName())
                .lastName(registerUserDTO.getLastName())
                .phoneNumber(registerUserDTO.getPhoneNumber())
                .build();
    }
}
