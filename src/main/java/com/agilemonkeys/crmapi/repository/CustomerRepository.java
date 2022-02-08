package com.agilemonkeys.crmapi.repository;

import com.agilemonkeys.crmapi.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByNameAndSurname(String name, String surname);
}
