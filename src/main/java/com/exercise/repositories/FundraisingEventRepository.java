package com.exercise.repositories;


import com.exercise.entities.FundraisingEvent;
import com.exercise.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    boolean existsByNameAndCurrency(String name, Currency currency);
}