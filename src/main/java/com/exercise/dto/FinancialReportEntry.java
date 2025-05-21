package com.exercise.dto;

import com.exercise.model.Currency;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReportEntry {
    private String name;
    private BigDecimal amount;
    private Currency currency;
}