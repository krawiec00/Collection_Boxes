package com.exercise.collection_boxes;

import com.exercise.dto.AddMoneyRequest;
import com.exercise.dto.AssignBoxRequest;
import com.exercise.dto.FundraisingEventResponse;
import com.exercise.entities.CollectionBox;
import com.exercise.entities.FundraisingEvent;
import com.exercise.repositories.CollectionBoxRepository;
import com.exercise.repositories.FundraisingEventRepository;
import com.exercise.services.CollectionBoxService;
import com.exercise.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionBoxServiceEdgeCasesTest {

    @Mock private CollectionBoxRepository boxRepo;
    @Mock private FundraisingEventRepository eventRepo;
    @InjectMocks private CollectionBoxService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignBox_BoxNotFound_Throws404() {
        when(boxRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.assignBox(99L, new AssignBoxRequest(1L))
        );
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("Box not found"));
    }

    @Test
    void assignBox_EventNotFound_Throws404() {
        CollectionBox emptyBox = CollectionBox.builder()
                .id(1L).empty(true).assigned(false).money(new HashMap<>()).build();
        when(boxRepo.findById(1L)).thenReturn(Optional.of(emptyBox));
        when(eventRepo.findById(42L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.assignBox(1L, new AssignBoxRequest(42L))
        );
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("Event not found"));
    }

    @Test
    void addMoney_BoxNotFound_Throws404() {
        when(boxRepo.findById(5L)).thenReturn(Optional.empty());

        AddMoneyRequest req = AddMoneyRequest.builder()
                .currency(Currency.EUR).amount(10.0).build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.addMoney(5L, req)
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void addMoney_NonPositiveAmount_Throws400() {
        CollectionBox box = CollectionBox.builder()
                .id(2L).empty(true).assigned(false).money(new HashMap<>()).build();
        when(boxRepo.findById(2L)).thenReturn(Optional.of(box));

        AddMoneyRequest reqZero = AddMoneyRequest.builder()
                .currency(Currency.GBP).amount(0.0).build();
        AddMoneyRequest reqNegative = AddMoneyRequest.builder()
                .currency(Currency.GBP).amount(-5.0).build();

        ResponseStatusException exZero = assertThrows(ResponseStatusException.class, () ->
                service.addMoney(2L, reqZero)
        );
        assertEquals(400, exZero.getStatusCode().value());

        ResponseStatusException exNeg = assertThrows(ResponseStatusException.class, () ->
                service.addMoney(2L, reqNegative)
        );
        assertEquals(400, exNeg.getStatusCode().value());
    }

    @Test
    void transfer_BoxNotFound_Throws404() {
        when(boxRepo.findById(7L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.transfer(7L)
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void transfer_UnassignedBox_Throws400() {
        CollectionBox box = CollectionBox.builder()
                .id(3L).assigned(false).empty(false)
                .money(Map.of(Currency.EUR, 10.0)).build();
        when(boxRepo.findById(3L)).thenReturn(Optional.of(box));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.transfer(3L)
        );
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("not assigned"));
    }

    @Test
    void transfer_Success_BoxClearedAndEventUpdated() {
        FundraisingEvent event = FundraisingEvent.builder()
                .id(10L).name("Test").currency(Currency.PLN)
                .accountBalance(BigDecimal.ZERO).build();
        CollectionBox box = CollectionBox.builder()
                .id(4L).assigned(true).empty(false).assignedEvent(event)
                .money(new HashMap<>(Map.of(Currency.EUR, 2.0, Currency.PLN, 10.0)))
                .build();

        when(boxRepo.findById(4L)).thenReturn(Optional.of(box));

        FundraisingEventResponse resp = service.transfer(4L);

        assertTrue(box.isEmpty());
        assertFalse(box.isAssigned());
        assertNull(box.getAssignedEvent());

        assertEquals(0, resp.getAccountBalance().compareTo(BigDecimal.valueOf(19.0)));
    }
}