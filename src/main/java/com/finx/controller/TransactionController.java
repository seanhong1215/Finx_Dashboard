package com.finx.controller;

import com.finx.model.Transaction;
import com.finx.security.CustomUserDetails;
import com.finx.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public String transactions(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            Model model) {

        Long userId = currentUser.getId();
        List<Transaction> transactions;

        if (category != null && !category.isEmpty()) {
            transactions = transactionService.findByCategory(userId, category);
        } else if (type != null && !type.isEmpty()) {
            transactions = transactionService.findByDirection(
                    userId, Transaction.Direction.valueOf(type));
        } else {
            transactions = transactionService.findAll(userId);
        }

        long totalIn  = transactions.stream().filter(Transaction::isIncome).count();
        long totalOut = transactions.size() - totalIn;

        model.addAttribute("transactions",     transactions);
        model.addAttribute("totalCount",       transactions.size());
        model.addAttribute("totalIn",          totalIn);
        model.addAttribute("totalOut",         totalOut);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedType",     type);
        model.addAttribute("activePage",       "transactions");

        return "transactions/index";
    }
}
