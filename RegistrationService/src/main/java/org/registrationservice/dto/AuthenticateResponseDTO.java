package org.registrationservice.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Class that hold the response in case of successfully authentication.
 */
@Data
@Builder
public class AuthenticateResponseDTO {
    private String jwtToken;

    private String refreshToken;

    @Override
    public String toString(){
        return "JWT Token : " + jwtToken + "\n" + "Refresh Token : " + refreshToken;
    }
}
