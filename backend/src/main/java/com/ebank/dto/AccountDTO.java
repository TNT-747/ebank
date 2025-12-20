package com.ebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private Long id;
    private String rib;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
    private String clientName;
}
