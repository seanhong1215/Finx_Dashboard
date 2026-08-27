package com.finx.repository;

import com.finx.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderBySpentOnDescCreatedAtDesc(Long userId);

    List<Expense> findTop8ByUserIdOrderBySpentOnDescCreatedAtDesc(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND (:category IS NULL OR e.category = :category) " +
           "AND (:creditCardId IS NULL OR e.creditCard.id = :creditCardId) " +
           "AND e.spentOn BETWEEN :fromDate AND :toDate " +
           "ORDER BY e.spentOn DESC, e.createdAt DESC")
    List<Expense> search(@Param("userId") Long userId,
                         @Param("fromDate") LocalDate fromDate,
                         @Param("toDate") LocalDate toDate,
                         @Param("category") String category,
                         @Param("creditCardId") Long creditCardId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId " +
           "AND e.spentOn BETWEEN :fromDate AND :toDate")
    BigDecimal sumByPeriod(@Param("userId") Long userId,
                           @Param("fromDate") LocalDate fromDate,
                           @Param("toDate") LocalDate toDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.spentOn BETWEEN :fromDate AND :toDate")
    BigDecimal sumByPeriodForAll(@Param("fromDate") LocalDate fromDate,
                                 @Param("toDate") LocalDate toDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.creditCard.id = :creditCardId " +
           "AND e.spentOn BETWEEN :fromDate AND :toDate")
    BigDecimal sumByCardAndPeriod(@Param("creditCardId") Long creditCardId,
                                  @Param("fromDate") LocalDate fromDate,
                                  @Param("toDate") LocalDate toDate);

    long countByUserId(Long userId);
}
