package com.exercise.services;

import com.exercise.dto.CreateFundraisingEventRequest;
import com.exercise.dto.FinancialReportEntry;
import com.exercise.dto.FundraisingEventResponse;
import com.exercise.entities.FundraisingEvent;
import com.exercise.repositories.FundraisingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FundraisingEventService {

    private final FundraisingEventRepository eventRepository;


    public FundraisingEventResponse createEvent(CreateFundraisingEventRequest request) {
        FundraisingEvent entity = FundraisingEvent.builder()
                .name(request.getName())
                .currency(request.getCurrency())
                .accountBalance(BigDecimal.ZERO)
                .build();

        FundraisingEvent saved = eventRepository.save(entity);
        return mapToResponse(saved);
    }

    public List<FundraisingEventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FundraisingEventResponse mapToResponse(FundraisingEvent event) {
        return FundraisingEventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .currency(event.getCurrency())
                .accountBalance(event.getAccountBalance())
                .build();
    }

    public List<FinancialReportEntry> getFinancialReport() {
        return eventRepository.findAll().stream()
                .map(event -> FinancialReportEntry.builder()
                        .name(event.getName())
                        .amount(event.getAccountBalance())
                        .currency(event.getCurrency())
                        .build())
                .collect(Collectors.toList());
    }
}

