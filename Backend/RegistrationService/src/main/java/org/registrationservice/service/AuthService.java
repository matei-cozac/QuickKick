package org.registrationservice.service;

import org.registrationservice.dto.AuthenticateResponseDTO;
import org.registrationservice.dto.LoginDTO;
import org.registrationservice.dto.RegisterAdministratorDTO;
import org.registrationservice.dto.RegisterUserDTO;
import org.registrationservice.exception.ExpiredConfirmationTokenException;
import org.registrationservice.exception.InvalidParameterException;
import org.registrationservice.mapper.AccountMapper;
import org.registrationservice.mapper.AdministratorMapper;
import org.registrationservice.mapper.UserMapper;
import org.registrationservice.model.ConfirmationToken;
import org.registrationservice.model.Role;
import org.registrationservice.repository.AccountRepository;
import org.registrationservice.repository.AdministratorRepository;
import org.registrationservice.repository.ConfirmationTokenRepository;
import org.registrationservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AccountRepository accountRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final AdministratorMapper administratorMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final KafkaService kafkaService;

    public AuthService(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository,
                       AccountRepository accountRepository, AdministratorRepository administratorRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtService jwtService, AccountMapper accountMapper, UserMapper userMapper,
                       AdministratorMapper administratorMapper, KafkaService kafkaService) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.administratorRepository = administratorRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountMapper = accountMapper;
        this.userMapper = userMapper;
        this.administratorMapper = administratorMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.kafkaService = kafkaService;
    }

    /**
     * Method that contains the business logic for the registration of the user. It checks if the email
     * is associated with a user. If not, the password will be encrypted using the BCrypt encoder before
     * persisting the data. An email will be also sent and the user must access the link within it to
     * confirm his account.
     * @param registerDto
     * The Dto that contains information inserted by user for registration.
     * @return
     * The UUID of the newly registered user.
     * @throws
     * InvalidParameterException exception is thrown if the email is associated with another user.
     */
    public Integer registerUser(RegisterUserDTO registerDto) {
        if(accountRepository.existsByEmail(registerDto.getEmail())){
            throw new InvalidParameterException("Email already in use.");
        }
        else if(accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())){
            throw new InvalidParameterException("Phone number already in use.");
        }
        var confirmationToken = createConfirmationToken();
        var account = accountMapper.fromRegisterUserDTO(registerDto);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Role.ROLE_USER);
        account.setConfirmationToken(confirmationToken);

        var user = userMapper.fromRegisterDTO(registerDto);
        user.setAccount(account);
        user = userRepository.save(user);

        //send through kafka to KafkaService for notifying user of account creation
        kafkaService.sendRegisterUserNotification(user.getLastName(), user.getFirstName(),
                user.getAccount().getUsername(), confirmationToken.getToken());

        return user.getId();
    }

    /**
     * Method that contains the business logic for registering an administrator. The method creates
     * the entities and saves them into the database. After that, the details are sent to AdminService
     * trough kafka for confirmation on the details. The account will be usable after the admin validates
     * the account
     * @param registerDto
     * The Dto that contains information needed for registration inserted by the user.
     * @return
     * The id of the newly created administrator account. The account needs to be validated by
     * the admin in order to be used.
     */
    public Integer registerAdministrator(RegisterAdministratorDTO registerDto) {
        if(accountRepository.existsByEmail(registerDto.getEmail())){
            throw new InvalidParameterException("Email already in use.");
        }
        else if(accountRepository.existsByPhoneNumber(registerDto.getPhoneNumber())){
            throw new InvalidParameterException("Phone number already in use.");
        }
        else if(administratorRepository.existsByAddress(registerDto.getAddress())){
            throw new InvalidParameterException("Address already in use.");
        }
        else if(administratorRepository.existsByCui(registerDto.getCui())){
            throw new InvalidParameterException("CUI already in use.");
        }
        else if(administratorRepository.existsByIban(registerDto.getIban())){
            throw new InvalidParameterException("Iban already in use.");
        }
        else if(administratorRepository.existsByBusinessName(registerDto.getBusinessName())){
            throw new InvalidParameterException("Business name already in use.");
        }

        var account = accountMapper.fromRegisterAdministratorDTO(registerDto);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Role.ROLE_ADMINISTRATOR);
        account.setConfirmationToken(null);

        var administrator = administratorMapper.fromRegisterAdministratorDTO(registerDto);
        administrator.setAccount(account);
        administrator = administratorRepository.save(administrator);

        kafkaService.sendAdministratorRegisterRequest(administrator);

        return administrator.getId();
    }

    /**
     * Method that contains the business logic for activating an account. Upon the confirmation token received
     * from the HTTP request, we confirm the account or not. If the received token does not exist is the
     * database, an InvalidParameterException is thrown.
     * @param confirmationToken
     * The token associated with the account that we need to activate.
     */
    public void activateAccount(UUID confirmationToken) {
        Optional<ConfirmationToken> token = confirmationTokenRepository.findByToken(confirmationToken);
        if(token.isEmpty()){
            throw new InvalidParameterException("Confirmation token not associated with any account.");
        }

        if(token.get().getExpiresAt().isBefore(LocalDateTime.now())){
            deleteUserData(confirmationToken);
            throw new ExpiredConfirmationTokenException("Confirmation token is expired. Please register again.");
        }
        var confirmation_token = token.get();
        confirmation_token.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmation_token);
        var userOptional = userRepository.findByAccount_ConfirmationToken_Token(confirmationToken);
        if(userOptional.isEmpty()){
            throw new InvalidParameterException("Confirmation token not associated with any account.");
        }
        var user = userOptional.get();
        user.getAccount().setActivated(true);
        userRepository.save(user);
    }

    /**
     * Method that will have the business logic for authenticating a user.
     * It checks the credentials, and after will generate a jwt token and a refresh token.
     *
     * @param loginDto
     * The information needed for authenticating, received from the HTTP request.
     * @return It returns an AuthenticateResponseDTO object, that holds the jwt token and the refresh token.
     */
    public AuthenticateResponseDTO authenticate(LoginDTO loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword()));
        var accountOptional = accountRepository.findByEmail(loginDto.getEmail());
        if(accountOptional.isEmpty()){
            throw new InvalidParameterException("Invalid email.");
        }

        var account = accountOptional.get();
        var jwt = jwtService.generateToken(account);
        var refreshToken = jwtService.generateRefreshToken(account);

        return AuthenticateResponseDTO
                .builder()
                .jwtToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Creates a new confirmation token. Called when a user wants to register.
     * @return
     * An object of type ConfirmationToken.
     */
    private ConfirmationToken createConfirmationToken() {
        return ConfirmationToken
                .builder()
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .token(UUID.randomUUID())
                .build();
    }

    /**
     * Method that will delete all the data associated with a confirmation token.
     * This happens when the token is expired. This ensures that the user will register again.
     */
    private void deleteUserData(UUID confirmationToken) {
        var user = userRepository.findByAccount_ConfirmationToken_Token(confirmationToken);
        if(user.isEmpty()){
            throw new InvalidParameterException("Confirmation token not associated with any account.");
        }
        userRepository.delete(user.get());
    }
}
