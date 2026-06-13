package io.github.fatima797.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal User user) {

        ExpenseResponse response = expenseService.createExpense(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
