package com.exercise.entities;


import com.exercise.model.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "fundraising_event",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_event_name_currency",
                columnNames = {"name", "currency"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundraisingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(precision = 19, scale = 2)
    private BigDecimal accountBalance;
}