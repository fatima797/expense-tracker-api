package io.github.fatima797.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.github.fatima797.expensetracker.model.ExpenseCategory;

public record ExpenseResponse(
        UUID publicId,
        String description,
        BigDecimal amount,
        ExpenseCategory category,
        LocalDate date) {

}
