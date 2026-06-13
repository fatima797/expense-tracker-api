package io.github.fatima797.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.fatima797.expensetracker.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest(

        @Size(max = 255, min = 1, message = "Description must not exceed 255 characters") String description,

        @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than 0") @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places") BigDecimal amount,
        @NotNull ExpenseCategory category,
        @NotNull @PastOrPresent(message = "Date must not be in the future") LocalDate date) {

}
