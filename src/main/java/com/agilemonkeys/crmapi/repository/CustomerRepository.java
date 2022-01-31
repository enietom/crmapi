package com.agilemonkeys.crmapi.repository;

import com.agilemonkeys.crmapi.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}
