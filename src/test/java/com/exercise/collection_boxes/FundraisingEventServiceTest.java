package com.exercise.collection_boxes;

import com.exercise.dto.CreateFundraisingEventRequest;
import com.exercise.entities.FundraisingEvent;
import com.exercise.model.Currency;
import com.exercise.repositories.FundraisingEventRepository;
import com.exercise.services.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FundraisingEventServiceTest {

    @Mock
    private FundraisingEventRepository repo;

    @InjectMocks
    private FundraisingEventService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_Success() {
        // given
        CreateFundraisingEventRequest req = CreateFundraisingEventRequest.builder()
                .name("Charity One")
                .currency(Currency.EUR)
                .build();

        FundraisingEvent saved = FundraisingEvent.builder()
                .id(1L)
                .name("Charity One")
                .currency(Currency.EUR)
                .accountBalance(BigDecimal.ZERO)
                .build();

        when(repo.existsByNameAndCurrency("Charity One", Currency.EUR)).thenReturn(false);
        when(repo.save(any(FundraisingEvent.class))).thenReturn(saved);

        // when
        var resp = service.createEvent(req);

        // then
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        assertEquals("Charity One", resp.getName());
        assertEquals(Currency.EUR, resp.getCurrency());
        assertEquals(BigDecimal.ZERO, resp.getAccountBalance());
        verify(repo).save(any());
    }

    @Test
    void createEvent_Duplicate_Throws() {
        // given
        CreateFundraisingEventRequest req = CreateFundraisingEventRequest.builder()
                .name("Charity One")
                .currency(Currency.EUR)
                .build();
        when(repo.existsByNameAndCurrency("Charity One", Currency.EUR)).thenReturn(true);

        // when / then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.createEvent(req);
        });
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(Objects.requireNonNull(ex.getReason()).contains("already exists"));
        verify(repo, never()).save(any());
    }

    @Test
    void getFinancialReport_EmptyList() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        var report = service.getFinancialReport();
        assertNotNull(report);
        assertTrue(report.isEmpty());
        verify(repo).findAll();
    }
}