package com.exercise.dto;

import com.exercise.model.Currency;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundraisingEventResponse {
    private Long id;
    private String name;
    private Currency currency;
    private BigDecimal accountBalance;
}
