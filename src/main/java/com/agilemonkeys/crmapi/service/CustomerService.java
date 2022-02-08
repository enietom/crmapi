package com.agilemonkeys.crmapi.service;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.entity.CustomerEntity;
import com.agilemonkeys.crmapi.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

        if (!isValidCustomerDto(customerDto)) {
            throw new IllegalArgumentException("Customer information is incorrect");
        }

        CustomerEntity customerEntity = mapCustomerDtoToCustomerEntity(customerDto);

        CustomerEntity newCustomerEntity = customerRepository.save(customerEntity);

        CustomerDto newCustomerDto = mapCustomerEntityToCustomerDto(newCustomerEntity);

        log.debug("Customer created successfully {}", newCustomerDto);
        return newCustomerDto;
    }

    public void createCustomerBulk(List<CustomerDto> customerDtos) {
        List<CustomerEntity> customerEntities = customerDtos.stream()
            .filter(this::isValidCustomerDto)
            .map(this::mapCustomerDtoToCustomerEntity)
            .collect(Collectors.toList());

        customerEntities = customerRepository.saveAll(customerEntities);

        // TODO: Send an email with the summary of the uploads

        log.debug("{} Customers created successfully", customerEntities.size());
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

    private boolean isValidCustomerDto(CustomerDto customerDto) {
        if (customerDto.getName() == null) {
            return false;
        }
        Optional<CustomerEntity> existingCustomerEntity = customerRepository.findByNameAndSurname(customerDto.getName(), customerDto.getSurname());
        // If not customer with same name and surname is present, then is valid
        return existingCustomerEntity.isEmpty();
    }

    private CustomerDto mapCustomerEntityToCustomerDto(CustomerEntity customerEntity) {
        return CustomerDto.builder()
            .id(customerEntity.getId())
            .name(customerEntity.getName())
            .surname(customerEntity.getSurname())
            .photo(customerEntity.getPhoto())
            .lastModifiedBy(customerEntity.getLastModifiedBy())
            .lastModifiedDate(customerEntity.getLastModifiedDate())
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
