package com.exercise.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignBoxRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;
}