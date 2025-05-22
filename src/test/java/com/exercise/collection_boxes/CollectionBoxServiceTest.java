package com.exercise.collection_boxes;

import com.exercise.dto.AddMoneyRequest;
import com.exercise.dto.AssignBoxRequest;
import com.exercise.entities.CollectionBox;
import com.exercise.repositories.CollectionBoxRepository;
import com.exercise.repositories.FundraisingEventRepository;
import com.exercise.services.CollectionBoxService;
import com.exercise.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionBoxServiceTest {

    @Mock
    private CollectionBoxRepository boxRepo;

    @InjectMocks
    private CollectionBoxService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBox_ShouldReturnEmptyUnassigned() {
        CollectionBox saved = CollectionBox.builder()
                .id(1L)
                .assigned(false)
                .empty(true)
                .money(new HashMap<>())
                .build();
        when(boxRepo.save(any(CollectionBox.class))).thenReturn(saved);

        var resp = service.createBox();

        assertEquals(1L, resp.getId());
        assertFalse(resp.isAssigned());
        assertTrue(resp.isEmpty());
        verify(boxRepo).save(any());
    }

    @Test
    void assignBox_NotEmpty_Throws() {
        CollectionBox box = CollectionBox.builder()
                .id(1L)
                .empty(false)
                .assigned(false)
                .money(Map.of(Currency.EUR, 10.0))
                .build();
        when(boxRepo.findById(1L)).thenReturn(Optional.of(box));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.assignBox(1L, new AssignBoxRequest(5L));
        });
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void addMoney_Success() {
        CollectionBox box = CollectionBox.builder()
                .id(1L)
                .empty(true)
                .assigned(false)
                .money(new HashMap<>())
                .build();
        when(boxRepo.findById(1L)).thenReturn(Optional.of(box));

        AddMoneyRequest req = AddMoneyRequest.builder()
                .currency(Currency.GBP)
                .amount(20.0)
                .build();

        var resp = service.addMoney(1L, req);

        assertFalse(resp.isEmpty());
    }

    @Test
    void transfer_Unassigned_Throws() {
        CollectionBox box = CollectionBox.builder()
                .id(1L)
                .assigned(false)
                .empty(false)
                .money(Map.of(Currency.EUR, 10.0))
                .build();
        when(boxRepo.findById(1L)).thenReturn(Optional.of(box));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.transfer(1L);
        });
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("not assigned"));
    }

    @Test
    void unregisterBox_ShouldClearAndDelete() {
        Map<Currency, Double> initialMoney = new HashMap<>();
        initialMoney.put(Currency.PLN, 5.0);

        CollectionBox box = CollectionBox.builder()
                .id(1L)
                .empty(false)
                .assigned(true)
                .money(initialMoney)
                .build();

        when(boxRepo.findById(1L)).thenReturn(Optional.of(box));

        service.unregisterBox(1L);

        assertTrue(box.getMoney().isEmpty());
        assertTrue(box.isEmpty());
        assertFalse(box.isAssigned());
        verify(boxRepo).delete(box);
    }
}
