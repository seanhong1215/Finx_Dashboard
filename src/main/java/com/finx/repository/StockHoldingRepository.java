package com.finx.repository;

import com.finx.model.StockHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding, Long> {
    List<StockHolding> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(s.currentPrice * s.shares), 0) FROM StockHolding s WHERE s.user.id = :userId")
    BigDecimal sumMarketValueByUserId(@Param("userId") Long userId);
}
