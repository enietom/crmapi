package com.agilemonkeys.crmapi.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class AuthUserDto {

    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;
    @NotNull
    @NotEmpty
    private String firstName;
    private String lastName;
    private String roles;

}
