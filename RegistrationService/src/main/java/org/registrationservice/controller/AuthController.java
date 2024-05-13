package org.registrationservice.controller;

import org.registrationservice.dto.LoginDTO;
import org.registrationservice.dto.RegisterUserDTO;
import org.registrationservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

/**
 * Controller for registration, login and confirming account operations.
 */
@RestController
@RequestMapping("/api/v1.0/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for registering a user.
     * @param registerDto
     * The information needed for registering the user.
     * @return
     * If no exception is thrown during the registration of the user, the confirmation UUID is returned.
     */
    @PostMapping("/register-user")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerDto){
        var userId = authService.registerUser(registerDto);
        var uri = ServletUriComponentsBuilder.fromUriString("/api/users").path("/{userId}").buildAndExpand(userId).toUri();
        return ResponseEntity.created(uri).body("User registered successfully." +
                "\nUser id : " + userId);
    }

    /**
     * Endpoint for confirmation of a user account.
     * @param confirmationToken
     * The confirmation token associated with the user. This confirmation token was received by the user
     * in the email sent to him after the account was successfully created.
     * @return
     * The status of validation of the confirmation token.
     */
    @PostMapping("/confirm-account/{token}")
    public ResponseEntity<?> confirmAccount(@PathVariable(name = "token") UUID confirmationToken){
        authService.activateAccount(confirmationToken);
        return ResponseEntity.status(HttpStatus.OK).body("Account confirmed successfully");
    }

    /**
     * Endpoint for authenticating a user.
     * @param loginDTO
     * The information needed for login.
     * @return
     * TokenResponse object, which contains both a jwtToken and a refresh token.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginDTO loginDTO){
        var authResponse = authService.authenticate(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Login successfully.\n" +  authResponse);
    }
}
