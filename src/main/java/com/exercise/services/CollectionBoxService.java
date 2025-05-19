package com.exercise.services;

import com.exercise.dto.AddMoneyRequest;
import com.exercise.dto.AssignBoxRequest;
import com.exercise.dto.CollectionBoxResponse;
import com.exercise.entities.CollectionBox;
import com.exercise.entities.FundraisingEvent;
import com.exercise.repositories.CollectionBoxRepository;
import com.exercise.repositories.FundraisingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.exercise.model.Currency;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CollectionBoxService {

    private final CollectionBoxRepository boxRepository;
    private final FundraisingEventRepository eventRepository;

    public CollectionBoxResponse createBox() {
        CollectionBox box = CollectionBox.builder()
                .assigned(false)
                .empty(true)
                .money(new HashMap<>())
                .build();
        box = boxRepository.save(box);
        return mapToResponse(box);
    }

    public List<CollectionBoxResponse> getAllBoxes() {
        return boxRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void unregisterBox(Long boxId) {
        CollectionBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Box not found"));
        box.getMoney().clear();
        box.setEmpty(true);
        box.setAssigned(false);
        box.setAssignedEvent(null);
        boxRepository.delete(box);
    }

    public CollectionBoxResponse assignBox(Long boxId, AssignBoxRequest request) {
        CollectionBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Box not found"));
        if (!box.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Can assign only empty box");
        }
        FundraisingEvent event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Event not found"));

        box.setAssigned(true);
        box.setAssignedEvent(event);
        return mapToResponse(box);
    }

    public CollectionBoxResponse addMoney(Long boxId, AddMoneyRequest request) {
        CollectionBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Box not found"));
        Currency currency = request.getCurrency();
        Double amount = request.getAmount();
        if (amount <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Amount must be positive");
        }
        Map<Currency, Double> moneyMap = box.getMoney();
        moneyMap.merge(currency, amount, Double::sum);
        box.setEmpty(moneyMap.values().stream().allMatch(v -> v == 0.0));
        return mapToResponse(box);
    }

    private CollectionBoxResponse mapToResponse(CollectionBox box) {
        return CollectionBoxResponse.builder()
                .id(box.getId())
                .assigned(box.isAssigned())
                .empty(box.isEmpty())
                .money(Collections.unmodifiableMap(box.getMoney()))
                .build();
    }
}

