package com.agilemonkeys.crmapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CustomerDto {

    private Long id;
    private String name;
    private String surname;
    private String photo;
    private Long lastModifiedBy;
    private Instant lastModifiedDate;

}
