package org.registrationservice.mapper;

import org.registrationservice.dto.RegisterAdministratorDTO;
import org.registrationservice.model.Administrator;
import org.springframework.stereotype.Component;

@Component
public class AdministratorMapper {

    /**
     * Mapper from a RegisterAdministratorDTO to an entity of type Administrator
     * @param dto
     * Details of the administrator, received from the HTTP requests.
     * @return
     * An entity of type Administrator, with the details provided in the DTO.
     */
    public Administrator fromRegisterAdministratorDTO(RegisterAdministratorDTO dto) {
        Administrator administrator = new Administrator();
        administrator.setIban(dto.getIban());
        administrator.setCui(dto.getCui());
        administrator.setAddress(dto.getAddress());
        administrator.setBusinessName(dto.getBusinessName());

        return administrator;
    }
}
