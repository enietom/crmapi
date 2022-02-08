package com.agilemonkeys.crmapi.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    private Long id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String firstName;
    private String lastName;
    private String roles;
    private Long lastModifiedBy;
    private Instant lastModifiedDate;

}
