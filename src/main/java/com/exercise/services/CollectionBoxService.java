package com.exercise.services;

import com.exercise.dto.AddMoneyRequest;
import com.exercise.dto.AssignBoxRequest;
import com.exercise.dto.CollectionBoxResponse;
import com.exercise.dto.FundraisingEventResponse;
import com.exercise.entities.CollectionBox;
import com.exercise.entities.FundraisingEvent;
import com.exercise.repositories.CollectionBoxRepository;
import com.exercise.repositories.FundraisingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.exercise.model.Currency;

import java.math.BigDecimal;
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

    public FundraisingEventResponse transfer(Long boxId) {
        CollectionBox box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Box not found"));

        if (!box.isAssigned() || box.getAssignedEvent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box is not assigned to any event");
        }
        FundraisingEvent event = box.getAssignedEvent();
        Currency target = event.getCurrency();

        // Bezpośrednie kursy między walutami
        // Key: źródłowa, Value: mapy docelowych kursów
        Map<Currency, Map<Currency, BigDecimal>> rates = Map.of(
                Currency.EUR, Map.of(
                        Currency.EUR, BigDecimal.ONE,
                        Currency.PLN, BigDecimal.valueOf(4.5),
                        Currency.GBP, BigDecimal.valueOf(0.85)
                ),
                Currency.PLN, Map.of(
                        Currency.EUR, BigDecimal.valueOf(0.22),
                        Currency.PLN, BigDecimal.ONE,
                        Currency.GBP, BigDecimal.valueOf(0.19)  // przykładowo
                ),
                Currency.GBP, Map.of(
                        Currency.EUR, BigDecimal.valueOf(1.17),
                        Currency.PLN, BigDecimal.valueOf(5.2),
                        Currency.GBP, BigDecimal.ONE
                )
        );

        BigDecimal total = box.getMoney().entrySet().stream()
                .map(e -> {
                    BigDecimal amount = BigDecimal.valueOf(e.getValue());
                    BigDecimal rate = rates.get(e.getKey()).get(target);
                    return amount.multiply(rate);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aktualizacja salda eventu
        event.setAccountBalance(event.getAccountBalance().add(total));

        // Opróżnienie puszki
        box.getMoney().clear();
        box.setEmpty(true);
        box.setAssigned(false);
        box.setAssignedEvent(null);

        return FundraisingEventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .currency(event.getCurrency())
                .accountBalance(event.getAccountBalance())
                .build();
    }


    private CollectionBoxResponse mapToResponse(CollectionBox box) {
        return CollectionBoxResponse.builder()
                .id(box.getId())
                .assigned(box.isAssigned())
                .empty(box.isEmpty())
                .build();
    }
}

