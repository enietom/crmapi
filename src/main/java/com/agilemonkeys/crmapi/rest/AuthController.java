package com.agilemonkeys.crmapi.rest;

import com.agilemonkeys.crmapi.dto.AuthUserDto;
import com.agilemonkeys.crmapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthUserDto registerUser(@RequestBody AuthUserDto authUserDto) {
        return userService.registerUser(authUserDto);
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="There was an error validating your request. Please check your input.")// 400
    @ExceptionHandler(BadCredentialsException.class)
    public void notFound() {
    }

}
