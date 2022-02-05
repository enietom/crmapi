package com.agilemonkeys.crmapi.service;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.entity.CustomerEntity;
import com.agilemonkeys.crmapi.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getCustomers() {
        log.debug("Getting customers");
        List<CustomerEntity> customerEntities = customerRepository.findAll();

        List<CustomerDto> result = customerEntities.stream().map(
            this::mapCustomerEntityToCustomerDto
        ).collect(Collectors.toList());

        log.debug("{} customers found", result.size());

        return result;
    }

    public CustomerDto getCustomer(Long customerId) {
        log.debug("Getting customer with id: {}", customerId);
        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow();

        CustomerDto customerDto = mapCustomerEntityToCustomerDto(customerEntity);

        log.debug("Customer found {} {} {}", customerId, customerDto.getName(), customerDto.getSurname());

        return customerDto;
    }

    public CustomerDto createCustomer(CustomerDto customerDto) {
        log.debug("Creating new customer");

        // TODO: Validations?
        // Question: Image uploads should be able to be manage?
        CustomerEntity customerEntity = mapCustomerDtoToCustomerEntity(customerDto);

        CustomerEntity newCustomerEntity = customerRepository.save(customerEntity);

        CustomerDto newCustomerDto = mapCustomerEntityToCustomerDto(newCustomerEntity);

        log.debug("Customer created successfully {}", newCustomerDto);

        return newCustomerDto;
    }

    public CustomerDto updateCustomer(Long customerId, CustomerDto customerDto) {
        log.debug("Updating customer with id {}", customerId);

        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow();

        customerEntity.setName(customerDto.getName());
        customerEntity.setSurname(customerDto.getSurname());
        customerEntity.setPhoto(customerDto.getPhoto());

        customerRepository.save(customerEntity);

        CustomerDto updatedCustomerDto = mapCustomerEntityToCustomerDto(customerEntity);

        log.debug("Customer updated successfully {}", updatedCustomerDto);

        return updatedCustomerDto;
    }

    public CustomerDto deleteCustomer(Long customerId) {
        log.debug("Deleting customer with id {}", customerId);

        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow();

        CustomerDto deletedCustomerDto = mapCustomerEntityToCustomerDto(customerEntity);

        customerRepository.delete(customerEntity);

        log.debug("Customer deleted successfully {}", deletedCustomerDto);

        return deletedCustomerDto;
    }

    private CustomerDto mapCustomerEntityToCustomerDto(CustomerEntity customerEntity) {
        return CustomerDto.builder()
            .id(customerEntity.getId())
            .name(customerEntity.getName())
            .surname(customerEntity.getSurname())
            .photo(customerEntity.getPhoto())
            .build();
    }

    private CustomerEntity mapCustomerDtoToCustomerEntity(CustomerDto customerDto) {
        return CustomerEntity.builder()
            .name(customerDto.getName())
            .surname(customerDto.getSurname())
            .photo(customerDto.getPhoto())
            .build();
    }
}
