package com.agilemonkeys.crmapi.service;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.entity.CustomerEntity;
import com.agilemonkeys.crmapi.repository.CustomerRepository;
import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final NotificationService notificationService;

    public CustomerService(CustomerRepository customerRepository, NotificationService notificationService) {
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
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

    public void createCustomerBulk(List<CustomerDto> customerDtos) throws IOException {
        List<CustomerEntity> customerEntities = customerDtos.stream()
            .filter(this::isValidCustomerDto)
            .map(this::mapCustomerDtoToCustomerEntity)
            .collect(Collectors.toList());

        customerEntities = customerRepository.saveAll(customerEntities);

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(customerEntities.size());
        bodyBuilder.append(" customers created successfully!!! These are the following:");
        customerEntities
                .forEach(customer -> bodyBuilder
                    .append("\n > ")
                    .append(customer.getId())
                    .append(" - ")
                    .append(customer.getName())
                    .append(" ")
                    .append(customer.getSurname())
                );
        log.debug("{} Customers created successfully", customerEntities.size());

        if (!customerEntities.isEmpty()) {
            // Sending notification to admin
            notificationService.sendEmail("CRM API - CSV Upload was successful", bodyBuilder.toString());
        }
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

    public CustomerDto uploadCustomerPhoto(Long customerId, File file) {
        log.debug("Updating photo for customer with id {}", customerId);

        CustomerEntity customerEntity = customerRepository.findById(customerId).orElseThrow();

        Cloudinary cloudinary = new Cloudinary(Map.of(
            "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
            "api_key", System.getenv("CLOUDINARY_API_KEY"),
            "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));

        String photoName = "photo_" + customerId;
        try {
            Map uploadResponse = cloudinary.uploader().upload(file,
                Map.of("public_id", photoName));

            customerEntity.setPhoto((String) uploadResponse.get("url"));
            customerRepository.save(customerEntity);
        } catch (IOException e) {
            e.printStackTrace();

        }

        CustomerDto updatedCustomerDto = mapCustomerEntityToCustomerDto(customerEntity);

        log.debug("Customer photo updated successfully {}", updatedCustomerDto);
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
