package org.registrationservice.repository;

import org.registrationservice.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

    boolean existsByAddress(String address);

    boolean existsByIban(String iban);

    boolean existsByCui(String cui);

    boolean existsByBusinessName(String businessName);
}
