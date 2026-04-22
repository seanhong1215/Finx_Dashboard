package com.finx.controller;

import com.finx.model.StockHolding;
import com.finx.security.CustomUserDetails;
import com.finx.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Controller
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StockService stockService;

    @GetMapping
    public String stocks(@AuthenticationPrincipal CustomUserDetails currentUser,
                         Model model) {
        List<StockHolding> holdings = stockService.findByUser(currentUser.getId());
        DecimalFormat df2 = new DecimalFormat("#,##0.00");

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalCost        = BigDecimal.ZERO;
        List<Map<String, Object>> rows = new ArrayList<>();

        for (StockHolding h : holdings) {
            BigDecimal price     = h.getCurrentPrice() != null ? h.getCurrentPrice() : BigDecimal.ZERO;
            BigDecimal marketVal = price.multiply(h.getShares()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costVal   = h.getAvgCost().multiply(h.getShares()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pnl       = marketVal.subtract(costVal);
            BigDecimal changePct = h.getChangePct() != null ? h.getChangePct() : BigDecimal.ZERO;

            totalMarketValue = totalMarketValue.add(marketVal);
            totalCost        = totalCost.add(costVal);

            Map<String, Object> row = new HashMap<>();
            row.put("id",              h.getId());
            row.put("ticker",          h.getTicker());
            row.put("company",         h.getCompany());
            row.put("currentPrice",    df2.format(price));
            row.put("changePositive",  changePct.compareTo(BigDecimal.ZERO) >= 0);
            row.put("changePctStr",    (changePct.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + changePct + "%");
            row.put("shares",          h.getShares().stripTrailingZeros().toPlainString());
            row.put("marketValue",     df2.format(marketVal));
            row.put("avgCost",         df2.format(h.getAvgCost()));
            row.put("pnlPositive",     pnl.compareTo(BigDecimal.ZERO) >= 0);
            row.put("pnlStr",          (pnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + df2.format(pnl));
            row.put("rawShares",       h.getShares().toPlainString());
            row.put("rawAvgCost",      h.getAvgCost().toPlainString());
            row.put("rawCurrentPrice", price.toPlainString());
            row.put("rawChangePct",    changePct.toPlainString());
            rows.add(row);
        }

        BigDecimal totalPnl = totalMarketValue.subtract(totalCost);
        BigDecimal pnlPct   = totalCost.compareTo(BigDecimal.ZERO) > 0
            ? totalPnl.divide(totalCost, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        model.addAttribute("stockRows",        rows);
        model.addAttribute("totalMarketValue", totalMarketValue.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("totalPnl",         totalPnl.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("pnlPct",           pnlPct);
        model.addAttribute("isProfit",         totalPnl.compareTo(BigDecimal.ZERO) >= 0);
        model.addAttribute("activePage",       "stocks");
        return "stocks/index";
    }
}
