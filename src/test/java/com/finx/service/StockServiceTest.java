package com.finx.service;

import com.finx.dto.request.StockRequest;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.StockHolding;
import com.finx.model.User;
import com.finx.repository.StockHoldingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService unit tests")
class StockServiceTest {

    @Mock StockHoldingRepository stockHoldingRepository;
    @InjectMocks StockService stockService;

    @Test
    @DisplayName("findByUser should return holdings for given user")
    void findByUser_shouldReturnHoldings() {
        given(stockHoldingRepository.findByUserId(1L))
                .willReturn(Arrays.asList(new StockHolding(), new StockHolding()));

        List<StockHolding> result = stockService.findByUser(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("create should persist a new stock holding")
    void create_shouldPersistHolding() {
        StockRequest req = new StockRequest();
        req.setTicker("aapl");
        req.setCompany("Apple Inc.");
        req.setShares(new BigDecimal("10"));
        req.setAvgCost(new BigDecimal("170"));
        req.setCurrentPrice(new BigDecimal("185"));

        User owner = new User(); owner.setId(1L);
        StockHolding saved = StockHolding.builder()
                .id(1L).user(owner).ticker("AAPL")
                .shares(req.getShares()).avgCost(req.getAvgCost())
                .currentPrice(req.getCurrentPrice()).build();

        given(stockHoldingRepository.save(any(StockHolding.class))).willReturn(saved);

        StockHolding result = stockService.create(1L, req);

        assertThat(result.getTicker()).isEqualTo("AAPL");
        verify(stockHoldingRepository).save(any(StockHolding.class));
    }

    @Test
    @DisplayName("delete should throw when holding not owned by user")
    void delete_shouldThrow_whenNotOwned() {
        User otherUser = new User(); otherUser.setId(99L);
        StockHolding holding = StockHolding.builder().id(5L).user(otherUser).build();

        given(stockHoldingRepository.findById(5L)).willReturn(Optional.of(holding));

        assertThatThrownBy(() -> stockService.delete(1L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete should remove holding when owned by user")
    void delete_shouldRemoveHolding() {
        User owner = new User(); owner.setId(1L);
        StockHolding holding = StockHolding.builder().id(5L).user(owner).build();

        given(stockHoldingRepository.findById(5L)).willReturn(Optional.of(holding));

        stockService.delete(1L, 5L);

        verify(stockHoldingRepository).delete(holding);
    }
}
