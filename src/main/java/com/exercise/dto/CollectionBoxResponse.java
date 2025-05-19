package com.exercise.dto;

import com.exercise.model.Currency;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionBoxResponse {
    private Long id;
    private boolean assigned;
    private boolean empty;
    private Map<Currency, Double> money;
}