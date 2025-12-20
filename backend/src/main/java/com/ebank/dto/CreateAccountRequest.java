package com.ebank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotBlank(message = "RIB is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$", message = "Invalid RIB format")
    private String rib;

    @NotBlank(message = "Identity number is required")
    private String identityNumber;
}
