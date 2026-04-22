package com.finx.controller;

import com.finx.model.CryptoAsset;
import com.finx.security.CustomUserDetails;
import com.finx.service.CryptoService;
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
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    private static final Map<String, BigDecimal[]> MOCK_PRICES;
    static {
        MOCK_PRICES = new java.util.HashMap<>();
        MOCK_PRICES.put("BTC", new BigDecimal[]{new BigDecimal("67420"), new BigDecimal("2.4")});
        MOCK_PRICES.put("ETH", new BigDecimal[]{new BigDecimal("3580"),  new BigDecimal("-1.2")});
        MOCK_PRICES.put("SOL", new BigDecimal[]{new BigDecimal("142"),   new BigDecimal("5.8")});
        MOCK_PRICES.put("BNB", new BigDecimal[]{new BigDecimal("415"),   new BigDecimal("0.9")});
    }

    @GetMapping
    public String crypto(@AuthenticationPrincipal CustomUserDetails currentUser,
                         Model model) {
        List<CryptoAsset> assets = cryptoService.findByUser(currentUser.getId());
        DecimalFormat df2 = new DecimalFormat("#,##0.00");

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost  = BigDecimal.ZERO;
        List<Map<String, Object>> rows = new ArrayList<>();

        for (CryptoAsset asset : assets) {
            BigDecimal[] price = MOCK_PRICES.getOrDefault(asset.getSymbol(),
                new BigDecimal[]{BigDecimal.ONE, BigDecimal.ZERO});
            BigDecimal currentPrice = price[0];
            BigDecimal changePct    = price[1];
            BigDecimal marketVal    = currentPrice.multiply(asset.getAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costVal      = asset.getAvgCost().multiply(asset.getAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pnl          = marketVal.subtract(costVal);

            totalValue = totalValue.add(marketVal);
            totalCost  = totalCost.add(costVal);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id",             asset.getId());
            row.put("symbol",         asset.getSymbol());
            row.put("name",           asset.getName());
            row.put("color",          asset.getColor());
            row.put("currentPrice",   df2.format(currentPrice));
            row.put("changePositive", changePct.compareTo(BigDecimal.ZERO) >= 0);
            row.put("changePctStr",   (changePct.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + changePct + "%");
            row.put("amount",         asset.getAmount().stripTrailingZeros().toPlainString());
            row.put("marketValue",    df2.format(marketVal));
            row.put("avgCost",        df2.format(asset.getAvgCost()));
            row.put("pnlPositive",    pnl.compareTo(BigDecimal.ZERO) >= 0);
            row.put("pnlStr",         (pnl.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + df2.format(pnl));
            row.put("rawAvgCost",     asset.getAvgCost().toPlainString());
            rows.add(row);
        }

        BigDecimal totalPnl = totalValue.subtract(totalCost);
        BigDecimal pnlPct   = totalCost.compareTo(BigDecimal.ZERO) > 0
            ? totalPnl.divide(totalCost, 4, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        model.addAttribute("cryptoRows", rows);
        model.addAttribute("totalValue", totalValue.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("totalPnl",   totalPnl.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("pnlPct",     pnlPct);
        model.addAttribute("isProfit",   totalPnl.compareTo(BigDecimal.ZERO) >= 0);
        model.addAttribute("activePage", "crypto");
        model.addAttribute("assets",     assets);
        return "crypto/index";
    }
}
