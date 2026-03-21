package com.spendwise.dto.request;

import com.spendwise.model.RecurringFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRecurringExpenseToolRequest {

    private UUID categoryId;
    private String name;
    private BigDecimal amount;
    private String currency;
    private RecurringFrequency frequency;
    private LocalDate nextDueDate;
}
