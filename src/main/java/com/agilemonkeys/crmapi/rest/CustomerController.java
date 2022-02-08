package com.agilemonkeys.crmapi.rest;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.service.CustomerService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerDto> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDto getCustomer(@PathVariable Long customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.createCustomer(customerDto);
    }

    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE, "text/csv"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createCustomerBulk(HttpServletRequest request) throws IOException {
        log.debug("Creating customers using CSV file");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        int customerCount = 0;
        try {
            MappingIterator<Map<String,String>> it = mapper.readerFor(Map.class)
                .with(schema)
                .readValues(request.getInputStream());

            List<CustomerDto> customerDtos = new ArrayList<>();

            while (it.hasNext()) {
                Map<String,String> rowAsMap = it.next();

                CustomerDto customerDto = CustomerDto.builder()
                    .name(rowAsMap.get("name"))
                    .surname(rowAsMap.get("surname"))
                    .photo(rowAsMap.get("photo_url"))
                    .build();

                customerDtos.add(customerDto);

                customerCount++;
            }
            log.debug("{} customers found in CSV file", customerCount);

            customerService.createCustomerBulk(customerDtos);
        } catch (IOException e) {
            log.error("createCustomerBulk - Error {} \n...while trying to upload file", e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{customerId}")
    public CustomerDto updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDto customerDto) {
        return customerService.updateCustomer(customerId, customerDto);
    }

    @DeleteMapping("/{customerId}")
    public CustomerDto deleteCustomer(@PathVariable Long customerId) {
        return customerService.deleteCustomer(customerId);
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Invalid Customer Information")
    @ExceptionHandler(IllegalArgumentException.class)
    public void invalidCustomer() {
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Customer Not Found")// 404
    @ExceptionHandler(NoSuchElementException.class)
    public void notFound() {
    }

}
