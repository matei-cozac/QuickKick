package org.registrationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.registrationservice.dto.AuthenticateResponseDTO;
import org.registrationservice.model.Account;
import org.registrationservice.model.Role;
import org.registrationservice.model.User;
import org.registrationservice.repository.UserRepository;
import org.registrationservice.service.JwtService;
import org.registrationservice.service.KafkaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final KafkaService kafkaService;

    public CustomAuthenticationSuccessHandler(JwtService jwtService,
                                              UserRepository userRepository,
                                              KafkaService kafkaService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.kafkaService = kafkaService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        User user = getOrCreateUser(oidcUser);

        kafkaService.sendOAuth2Notification(user.getAccount().getUsername(),
                user.getFirstName(), user.getLastName());
        AuthenticateResponseDTO authResponse = createAuthResponse(user);

        // Include JWT and Refresh Token in the response
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(authResponse));
    }

    private User getOrCreateUser(OidcUser oidcUser) {
        Map<String, Object> attributes = oidcUser.getAttributes();
        String email = (String) attributes.get("email");

        var optionalUser = userRepository.findByAccount_Email(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        Account account = Account
                .builder()
                .oauth(true)
                .activated(true)
                .role(Role.ROLE_USER)
                .confirmationToken(null)
                .email(email)
                .build();

        String lastname = (String) attributes.get("family_name");
        String firstname = (String) attributes.get("given_name");
        User user = User
                .builder()
                .account(account)
                .firstName(firstname)
                .lastName(lastname)
                .build();
        return userRepository.save(user);
    }

    private AuthenticateResponseDTO createAuthResponse(User user) {
        String jwtToken = jwtService.generateToken(user.getAccount());
        String refreshToken = jwtService.generateRefreshToken(user.getAccount());

        // Build response
        return AuthenticateResponseDTO.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
