package org.registrationservice.repository;

import org.registrationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByAccount_Email(String email);

    Optional<User> findByAccount_ConfirmationToken_Token(UUID confirmationToken);

    Optional<User> findByAccount_Email(String email);
}
