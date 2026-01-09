package com.farmstock.service;

import com.farmstock.model.dto.ParsedSmsOrder;
import com.farmstock.model.dto.ParsedSmsOrder.ParsedOrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SmsParserService {

    private static final Pattern ITEM_PATTERN = Pattern.compile(
            "([a-záäčďéíľĺňóôŕšťúýžA-ZÁÄČĎÉÍĽĹŇÓÔŔŠŤÚÝŽ]+)\\s*(\\d+(?:[.,]\\d+)?)\\s*(kg|t|q|ks|l)?",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern ALT_ITEM_PATTERN = Pattern.compile(
            "(\\d+(?:[.,]\\d+)?)\\s*(kg|t|q|ks|l)?\\s+([a-záäčďéíľĺňóôŕšťúýžA-ZÁÄČĎÉÍĽĹŇÓÔŔŠŤÚÝŽ]+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final List<String> IGNORE_WORDS = List.of(
            "objednavka", "objednávka", "chcem", "prosim", "prosím", "potrebujem",
            "daj", "dajte", "poslite", "pošlite", "a", "aj", "alebo", "este", "ešte"
    );

    public ParsedSmsOrder parse(String smsText) {
        if (smsText == null || smsText.isBlank()) {
            return ParsedSmsOrder.builder()
                    .valid(false)
                    .errorMessage("Prázdna SMS správa")
                    .build();
        }

        log.debug("Parsing SMS: {}", smsText);
        String normalizedText = normalizeText(smsText);
        List<ParsedOrderItem> items = new ArrayList<>();

        items.addAll(extractItems(normalizedText, ITEM_PATTERN, true));

        if (items.isEmpty()) {
            items.addAll(extractItems(normalizedText, ALT_ITEM_PATTERN, false));
        }

        if (items.isEmpty()) {
            return ParsedSmsOrder.builder()
                    .valid(false)
                    .errorMessage("Nepodarilo sa rozpoznat objednavku. Format: kukurica 500kg")
                    .build();
        }

        return ParsedSmsOrder.builder()
                .valid(true)
                .items(items)
                .build();
    }

    private List<ParsedOrderItem> extractItems(String text, Pattern pattern, boolean productFirst) {
        List<ParsedOrderItem> items = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String product;
            String quantityStr;
            String unit;

            if (productFirst) {
                product = matcher.group(1).trim().toLowerCase();
                quantityStr = matcher.group(2).replace(",", ".");
                unit = matcher.group(3) != null ? matcher.group(3).toLowerCase() : "kg";
            } else {
                quantityStr = matcher.group(1).replace(",", ".");
                unit = matcher.group(2) != null ? matcher.group(2).toLowerCase() : "kg";
                product = matcher.group(3).trim().toLowerCase();
            }

            if (IGNORE_WORDS.contains(product)) {
                continue;
            }

            product = normalizeProductName(product);

            try {
                BigDecimal quantity = new BigDecimal(quantityStr);
                quantity = convertToKg(quantity, unit);
                
                if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                    items.add(ParsedOrderItem.builder()
                            .productName(product)
                            .quantity(quantity)
                            .unit("kg")
                            .build());
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse quantity: {}", quantityStr);
            }
        }

        return items;
    }

    private String normalizeText(String text) {
        return text.toLowerCase()
                .replaceAll("[,;]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String normalizeProductName(String product) {
        if (product.endsWith("ice")) {
            return product.substring(0, product.length() - 1) + "a";
        }
        if (product.endsWith("e") && product.length() > 3) {
            return product.substring(0, product.length() - 1) + "a";
        }
        return product;
    }

    private BigDecimal convertToKg(BigDecimal quantity, String unit) {
        if (unit == null) return quantity;
        return switch (unit.toLowerCase()) {
            case "t" -> quantity.multiply(new BigDecimal("1000"));
            case "q" -> quantity.multiply(new BigDecimal("100"));
            default -> quantity;
        };
    }
}