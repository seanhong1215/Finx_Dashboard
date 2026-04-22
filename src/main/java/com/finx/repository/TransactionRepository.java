package com.finx.repository;

import com.finx.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 全部交易（依日期降序）
    List<Transaction> findByAccountIdOrderByTransactedAtDesc(Long accountId);

    // 最近 10 筆
    List<Transaction> findTop10ByAccountIdOrderByTransactedAtDesc(Long accountId);

    // 依類別篩選
    List<Transaction> findByAccountIdAndCategory(Long accountId, String category);

    // 依方向篩選（IN/OUT）
    List<Transaction> findByAccountIdAndDirection(Long accountId, Transaction.Direction direction);

    // 依方向計數
    long countByAccountIdAndDirection(Long accountId, Transaction.Direction direction);

    // 本月支出總計
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId AND t.direction = 'OUT' " +
           "AND YEAR(t.transactedAt) = :year AND MONTH(t.transactedAt) = :month")
    BigDecimal sumExpenseByMonth(@Param("accountId") Long accountId,
                                 @Param("year") int year,
                                 @Param("month") int month);

    // 本月收入總計
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId AND t.direction = 'IN' " +
           "AND YEAR(t.transactedAt) = :year AND MONTH(t.transactedAt) = :month")
    BigDecimal sumIncomeByMonth(@Param("accountId") Long accountId,
                                @Param("year") int year,
                                @Param("month") int month);
}
