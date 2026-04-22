package com.finx.service;

import com.finx.dto.request.StockRequest;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.StockHolding;
import com.finx.model.User;
import com.finx.repository.StockHoldingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockHoldingRepository stockHoldingRepository;

    @Transactional(readOnly = true)
    public List<StockHolding> findByUser(Long userId) {
        return stockHoldingRepository.findByUserId(userId);
    }

    @Transactional
    public StockHolding create(Long userId, StockRequest req) {
        User user = new User();
        user.setId(userId);
        StockHolding holding = StockHolding.builder()
                .user(user)
                .ticker(req.getTicker().toUpperCase())
                .company(req.getCompany())
                .shares(req.getShares())
                .avgCost(req.getAvgCost())
                .currentPrice(req.getCurrentPrice())
                .changePct(req.getChangePct())
                .build();
        StockHolding saved = stockHoldingRepository.save(holding);
        log.info("Created stock holding id={} for userId={}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public StockHolding update(Long userId, Long holdingId, StockRequest req) {
        StockHolding holding = findOwnedHolding(userId, holdingId);
        holding.setTicker(req.getTicker().toUpperCase());
        holding.setCompany(req.getCompany());
        holding.setShares(req.getShares());
        holding.setAvgCost(req.getAvgCost());
        holding.setCurrentPrice(req.getCurrentPrice());
        holding.setChangePct(req.getChangePct());
        return stockHoldingRepository.save(holding);
    }

    @Transactional
    public void delete(Long userId, Long holdingId) {
        StockHolding holding = findOwnedHolding(userId, holdingId);
        stockHoldingRepository.delete(holding);
        log.info("Deleted stock holding id={} for userId={}", holdingId, userId);
    }

    private StockHolding findOwnedHolding(Long userId, Long holdingId) {
        StockHolding holding = stockHoldingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("StockHolding", holdingId));
        if (!holding.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("StockHolding", holdingId);
        }
        return holding;
    }
}
