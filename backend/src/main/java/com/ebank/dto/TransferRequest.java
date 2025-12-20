package com.ebank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotBlank(message = "Source RIB is required")
    private String sourceRib;

    @NotBlank(message = "Destination RIB is required")
    private String destinationRib;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Motif is required")
    private String motif;
}
