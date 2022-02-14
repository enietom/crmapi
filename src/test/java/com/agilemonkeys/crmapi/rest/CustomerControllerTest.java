package com.agilemonkeys.crmapi.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agilemonkeys.crmapi.dto.CustomerDto;
import com.agilemonkeys.crmapi.repository.UserRepository;
import com.agilemonkeys.crmapi.service.CustomerService;
import com.agilemonkeys.crmapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomerService customerService;


    // SecurityConfig need an instance of userService
    // TODO: Explore different ways to configure context
    @TestConfiguration
    static class CustomerControllerTestContextConfiguration {
        @MockBean
        private UserRepository userRepository;
        @Bean
        public UserService userService() {
            return new UserService(userRepository) {
                //
            };
        }
    }

    @Test
    public void getCustomers_thenReturnJsonArray() throws Exception {
        CustomerDto customerDto = CustomerDto.builder()
            .id(1L)
            .name("Name")
            .surname("Surname")
            .build();

        given(customerService.getCustomers()).willReturn(List.of(customerDto));

        mvc.perform(get("/customers").with(user("user").password("password").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(customerDto.getName())));
    }

    @Test
    public void getCustomers_noAuth_return401() throws Exception {
        CustomerDto customerDto = CustomerDto.builder()
            .id(1L)
            .name("Name")
            .surname("Surname")
            .build();

        given(customerService.getCustomers()).willReturn(List.of(customerDto));

        mvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void getCustomer_thenReturnJson() throws Exception {
        Long customerId = 1L;
        CustomerDto customerDto = CustomerDto.builder()
            .id(customerId)
            .name("Name")
            .surname("Surname")
            .build();

        given(customerService.getCustomer(customerId)).willReturn(customerDto);

        mvc.perform(get("/customers/{id}", customerId).with(user("user").password("password").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.name", is(customerDto.getName())))
            .andExpect(jsonPath("@.surname", is(customerDto.getSurname())));
    }

    @Test
    public void createCustomer() throws Exception {
        CustomerDto customerDto = CustomerDto.builder()
            .name("Name")
            .surname("Surname")
            .build();

        given(customerService.createCustomer(customerDto)).willReturn(customerDto);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(customerDto);

        mvc.perform(post("/customers").with(user("user").password("password").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("@.name", is(customerDto.getName())))
            .andExpect(jsonPath("@.surname", is(customerDto.getSurname())));
    }

    @Ignore
    @Test
    void createCustomerBulk() {
    }

    @Ignore
    @Test
    void updateCustomer() {
    }

    @Ignore
    @Test
    void uploadCustomerPhoto() {
    }

    @Ignore
    @Test
    void deleteCustomer() {
    }
}