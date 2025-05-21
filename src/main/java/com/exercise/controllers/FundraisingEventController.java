package com.exercise.controllers;

import com.exercise.dto.CreateFundraisingEventRequest;
import com.exercise.dto.FinancialReportEntry;
import com.exercise.dto.FundraisingEventResponse;
import com.exercise.services.FundraisingEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class FundraisingEventController {

    private final FundraisingEventService eventService;


    @PostMapping
    public ResponseEntity<FundraisingEventResponse> createEvent(
            @Valid @RequestBody CreateFundraisingEventRequest request) {
        FundraisingEventResponse response = eventService.createEvent(request);
        URI location = URI.create(String.format("/api/events/%d", response.getId()));
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FundraisingEventResponse>> getAllEvents() {
        List<FundraisingEventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/report")
    public ResponseEntity<List<FinancialReportEntry>> getFinancialReport() {
        List<FinancialReportEntry> report = eventService.getFinancialReport();
        return ResponseEntity.ok(report);
    }
}