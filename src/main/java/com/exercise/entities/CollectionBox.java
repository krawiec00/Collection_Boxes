package com.exercise.entities;

import com.exercise.model.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean assigned;

    private boolean empty;

    @ElementCollection
    @CollectionTable(name = "box_money", joinColumns = @JoinColumn(name = "box_id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "amount")
    private Map<Currency, Double> money = new HashMap<>();

    @ManyToOne
    private FundraisingEvent assignedEvent;
}
