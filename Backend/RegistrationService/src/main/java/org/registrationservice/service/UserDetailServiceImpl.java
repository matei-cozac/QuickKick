package org.registrationservice.service;

import org.registrationservice.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * An implementation of the UserDetailsService  interface. Provides functionalities for registering and
 * authenticating the user.
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    // The repository for the user entity.
    private final AccountRepository accountRepository;

    public UserDetailServiceImpl(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email).orElseThrow();
    }
}
