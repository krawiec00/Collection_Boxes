package com.exercise.dto;

import com.exercise.model.Currency;
import jakarta.validation.constraints.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFundraisingEventRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Currency is mandatory")
    private Currency currency;
}
