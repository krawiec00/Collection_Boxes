package com.exercise.repositories;

import com.exercise.entities.CollectionBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
}