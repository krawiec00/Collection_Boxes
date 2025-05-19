package com.exercise.dto;

import com.exercise.model.Currency;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMoneyRequest {
    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be > 0")
    private Double amount;
}