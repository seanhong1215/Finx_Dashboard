package com.finx.controller;

import com.finx.model.Card;
import com.finx.security.CustomUserDetails;
import com.finx.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardsController {

    private final CardService cardService;

    @GetMapping
    public String cards(@AuthenticationPrincipal CustomUserDetails currentUser,
                        Model model) {
        List<Card> cards = cardService.findActiveCards(currentUser.getId());

        BigDecimal totalLimit = cards.stream()
            .map(Card::getCreditLimit)
            .filter(l -> l != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cards",      cards);
        model.addAttribute("cardCount",  cards.size());
        model.addAttribute("totalLimit", totalLimit);
        model.addAttribute("activePage", "cards");

        return "cards/index";
    }
}
