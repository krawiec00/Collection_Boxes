package com.exercise.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionBoxResponse {
    private Long id;
    private boolean assigned;
    private boolean empty;
}