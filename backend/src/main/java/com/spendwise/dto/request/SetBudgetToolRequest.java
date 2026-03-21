package com.spendwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetBudgetToolRequest {

    private UUID categoryId;
    private YearMonth month;
    private BigDecimal amount;
}
