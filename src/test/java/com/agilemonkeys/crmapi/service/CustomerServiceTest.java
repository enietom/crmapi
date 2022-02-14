package com.agilemonkeys.crmapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.entity.CustomerEntity;
import com.agilemonkeys.crmapi.repository.CustomerRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private NotificationService notificationService;


    @Test
    public void getCustomers_returnsList() {
        CustomerEntity customerEntity = CustomerEntity.builder()
            .id(1L)
            .name("Name")
            .surname("Surname")
            .build();

        when(customerRepository.findAll()).thenReturn(List.of(customerEntity));

        List<CustomerDto> customerDtos = customerService.getCustomers();

        assertFalse(customerDtos.isEmpty());
        assertEquals("Name", customerDtos.get(0).getName());
        assertEquals("Surname", customerDtos.get(0).getSurname());
    }

    @Test
    public void getCustomer_returnsDto() {
        Long customerId = 1L;
        CustomerEntity customerEntity = CustomerEntity.builder()
            .id(customerId)
            .name("Name")
            .surname("Surname")
            .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));

        CustomerDto customerDto = customerService.getCustomer(customerId);

        assertNotNull(customerDto);
        assertEquals("Name", customerDto.getName());
        assertEquals("Surname", customerDto.getSurname());
    }

    @Test
    void createCustomer() {
    }

    @Test
    void createCustomerBulk() {
    }

    @Test
    void updateCustomer() {
    }

    @Test
    void uploadCustomerPhoto() {
    }

    @Test
    void deleteCustomer() {
    }
}