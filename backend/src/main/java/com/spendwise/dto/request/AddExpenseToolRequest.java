package com.spendwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddExpenseToolRequest {

    private UUID categoryId;
    private UUID receiptId;
    private BigDecimal amount;
    private String currency;
    private LocalDate spentAt;
    private String merchant;
    private String description;
}
