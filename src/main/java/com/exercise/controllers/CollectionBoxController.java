package com.exercise.controllers;

import com.exercise.dto.AddMoneyRequest;
import com.exercise.dto.AssignBoxRequest;
import com.exercise.dto.CollectionBoxResponse;
import com.exercise.dto.FundraisingEventResponse;
import com.exercise.services.CollectionBoxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
public class CollectionBoxController {

    private final CollectionBoxService boxService;


    @PostMapping
    public ResponseEntity<CollectionBoxResponse> createBox() {
        CollectionBoxResponse response = boxService.createBox();
        URI location = URI.create(String.format("/api/boxes/%d", response.getId()));
        return ResponseEntity.created(location).body(response);
    }


    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> getAllBoxes() {
        List<CollectionBoxResponse> boxes = boxService.getAllBoxes();
        return ResponseEntity.ok(boxes);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unregisterBox(@PathVariable("id") Long id) {
        boxService.unregisterBox(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/assign")
    public ResponseEntity<CollectionBoxResponse> assignBox(
            @PathVariable("id") Long id,
            @Valid @RequestBody AssignBoxRequest request) {
        CollectionBoxResponse response = boxService.assignBox(id, request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/add-money")
    public ResponseEntity<CollectionBoxResponse> addMoney(
            @PathVariable("id") Long id,
            @Valid @RequestBody AddMoneyRequest request) {
        CollectionBoxResponse response = boxService.addMoney(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<FundraisingEventResponse> transfer(
            @PathVariable("id") Long id) {
        FundraisingEventResponse updatedEvent = boxService.transfer(id);
        return ResponseEntity.ok(updatedEvent);
    }
}