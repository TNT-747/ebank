package com.ebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String identityNumber;
    private LocalDate birthDate;
    private String email;
    private String address;
    private String username;
}
